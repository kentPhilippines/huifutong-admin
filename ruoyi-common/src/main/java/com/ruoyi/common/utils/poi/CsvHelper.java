package com.ruoyi.common.utils.poi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Csv 工具
 *

 */
public class CsvHelper {
    private final static String charset = "GBK";

    private static class FieldData {
        Field field;
        CsvConvertVisitable converter;

        public FieldData(Field field, CsvConvertVisitable converter) {
            this.field = field;
            this.converter = converter;
        }
    }

    /**
     * 导出列表 CSV
     * @param items 要导出的数据列表
     * @param os 输出到的流
     * @param res HttpServletResponse（可选），如果指定了就会添加文件下载的头部
     * @param fileName 可选，文件名，用户下载的文件名，传入 res 有效
     * @param <T>
     * @throws IOException
     */
    public static <T extends Object> void writeCsv(List<T> items,
                                                   OutputStream os,
                                                   HttpServletResponse res,
                                                   String fileName) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException {
        if (res != null) {
            setHttpHeader(res, fileName);
            if (os == null)
                os = res.getOutputStream();
        }

        if (os == null) return;

        if (items == null || items.size() < 1) {
            os.flush();
            return;
        }

        // 筛选出拥有注解的字段
        List<String> titles = new ArrayList<String>();
        List<FieldData> csvFields = initCsvFields(items, titles);
        if (csvFields.size() < 1) {
            os.flush();
            return;
        }

        // 写入数据
        writeData(items, titles, csvFields, os);
    }

    // 设置下载用的 Http 响应头部
    private static void setHttpHeader(HttpServletResponse res, String fileName) {
        fileName = StringUtils.isEmpty(fileName) ? (generateRandomFileName() + ".csv") : fileName;
        // res.setHeader("content-type", "application/octet-stream");
        res.setHeader("content-type", "application/octet-stream; charset=" + charset);
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    }

    private static String generateRandomFileName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 初始化要输出的CSV字段
    private static <T extends Object> List<FieldData> initCsvFields(List<T> items, List<String> titles) {
        Class<? extends Object> cls = items.get(0).getClass();
        Field[] fields = cls.getDeclaredFields();

        // 筛选出拥有注解的字段
        List<FieldData> csvFields = new ArrayList<>();
        for(int i=0;i< fields.length;i++){
            CsvField item = fields[i].getAnnotation(CsvField.class);
            if (item == null || !item.ignore()) {
                CsvConvertVisitable converter = null;
                if (item.using() != null && item.using() != CsvConvertHandler.None.class) {
                    converter = ClassUtil.createInstance(item.using(), true);
                }
                csvFields.add(new FieldData(fields[i], converter));
                if (item == null)
                    titles.add("\"" + fields[i].getName() + "\"");
                else
                    titles.add("\"" + item.value() + "\"");
            }
        }
        return csvFields;
    }

    // 写入数据
    private static <T extends Object> void writeData(List<T> items, List<String> titles, List<FieldData> csvFields, OutputStream os)
            throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, JsonProcessingException {
        // 写入标题
        String text = stringArrayToCsvLine(titles.toArray(new String[titles.size()])) + "\n";
        byte[] buffer = text.getBytes(charset);
        long bufSize = buffer.length;
        os.write(buffer);

        // 写入内容
        List<Method> methods = fieldToMethods(csvFields, items.get(0));
        for (T item : items) {
            text = itemToString(item, methods, csvFields);
            if (text == null || text.isEmpty())
                continue;
            buffer = text.getBytes(charset);
            bufSize = bufSize + buffer.length;
            os.write(buffer);

            if (bufSize > 4096) {
                os.flush();
                bufSize = 0;
            }
        }

        if (bufSize > 0)
            os.flush();
    }

    private static List<Method> fieldToMethods(List<FieldData> csvFields, Object item) throws NoSuchMethodException {
        List<Method> result = new ArrayList<Method>();
        for (int i=0; i< csvFields.size(); i++) {
            String fieldName = csvFields.get(i).field.getName();
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = item.getClass().getMethod(methodName, null);
            result.add(method);
        }
        return result;
    }

    private static String itemToString(Object item, @NotNull List<Method> methods, List<FieldData> csvFields)
            throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        String[] values = new String[methods.size()];
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i=0; i< values.length; i++) {
            Method method = methods.get(i);
            FieldData field = csvFields.get(i);
            Object val = method.invoke(item, null);
            if (field.converter != null) {
                values[i] = objectMapper.writeValueAsString(field.converter.convert(val));
            } else if (val == null) {
                values[i] = "";
                continue;
            } else
                values[i] = objectMapper.writeValueAsString(val);
            if (values[i] == null || values[i].isEmpty())
                continue;
            if (!values[i].isEmpty() && (values[i].startsWith("{") || values[i].startsWith("[")))
                values[i] = "\"" + values[i].replace("\"", "\"\"") + "\"";
            else
                values[i] = values[i].replace("\\\"", "\"\"");
        }
        return stringArrayToCsvLine(values) + "\n";
    }

    public static String stringArrayToCsvLine(String[] text) {
        if (text == null)
            return "";

        int iMax = text.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(text[i]);
            if (i == iMax)
                return b.toString();
            b.append(",");
        }
    }
}