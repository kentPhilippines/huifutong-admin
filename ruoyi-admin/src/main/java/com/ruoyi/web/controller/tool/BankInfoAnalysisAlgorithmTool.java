package com.ruoyi.web.controller.tool;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 银行信息解析算法类
 *
 * @author water
 */
public enum BankInfoAnalysisAlgorithmTool {
    Instance;

    private static final Logger log = LoggerFactory.getLogger(BankInfoAnalysisAlgorithmTool.class);

    /**
     * 执行解析银行短信信息算法
     * 【中国农业银行】您尾号5672账户07月01日21:55向谢伦波完成转支交易人民币-2500.00，余额2058.08。
     * 【中国农业银行】您尾号5672账户07月01日21:50向宋宏财完成转支交易人民币-5940.00，余额4558.08。
     * 【中国农业银行】马驰于07月01日21:41向您尾号5672账户完成转存交易人民币12000.00，余额13998.08。
     * 【中国农业银行】您尾号5672账户07月01日21:43向段冬成完成转支交易人民币-1500.00，余额12498.08。
     * 【中国农业银行】您尾号5672账户07月01日21:46向史俊红完成转支交易人民币-2000.00，余额10498.08。
     * 【中国农业银行】您尾号5672账户07月01日21:46向史俊红完成转支交易人民币-2000.00，余额10498.08。
     * 【中国农业银行】您尾号5672账户07月01日21:17向孔雨利完成转支交易人民币-6812.00，余额1998.08。
     * 【中国农业银行】您尾号5672账户07月01日21:10向戚学燚完成转支交易人民币-1183.00，余额5405.08。
     * 【中国农业银行】您尾号5672账户07月01日20:15向赵东峰完成掌银转账交易人民币-5186.00，余额1488.08。
     * 您尾号0949卡7月1日21:17手机银行收入(网转)2,300元，余额3,974.01元，对方户名：段永祥，对方账户尾号：0371。【工商银行】
     * 您尾号0949卡7月1日21:19手机银行支出(网转)677元，余额3,297.01元，对方户名：唐艺钢，对方账户尾号：9462。【工商银行】
     * 您尾号0949卡7月1日21:44工商银行收入(跨行转出)1,000元，余额1,097.01元，对方户名：邓力，对方账户尾号：7796。【工商银行】
     * 您尾号0949卡7月1日21:34手机银行支出(跨行汇款)3,200元，余额97.01元，对方户名：王洪江，对方账户尾号：0640。【工商银行】
     * 您尾号0949卡7月1日20:06手机银行支出(网转)600元，余额1,674.01元，对方户名：康凯，对方账户尾号：6241。【工商银行】
     * 您尾号8101卡7月1日18:39工商银行收入(他行汇入)5元，余额586.27元，对方户名：应儒鹤，对方账户尾号：2954。
     * 【邮储银行】21年07月01日21:35武文文账户5242向您尾号954账户转账汇入金额500.00元，余额1639.23元。
     * 【邮储银行】21年07月01日21:21您尾号954账户向唐艺刚尾号9462账户汇款金额1800.00元，余额631.23元。
     * 【邮储银行】21年07月01日21:47黄捷浩账户4100向您尾号954账户他行来账金额1000.00元，余额1559.23元。
     * 【邮储银行】21年07月01日21:21您尾号954账户往账退回金额1800.00元，余额2431.23元。
     * 【邮储银行】21年07月01日20:07您尾号954账户银联入账金额600.00元，余额2746.23元。
     * 【邮储银行】尊敬的用户，您于21年07月01日通过手机银行向尾号9462的账户操作的跨行转账交易，由于账号、户名不符，钱款未到账，已退回原转出账户，金额1800.00元，注意查收。
     * 【邮储银行】验证码：373395（序号1786），您向施向阳尾号7959账户转账800.00元。任何人索要验证码均为诈骗，请勿泄露！
     * 【邮储银行】验证码：945693（序号1801），您向唐艺钢尾号9462账户转账1800.00元。任何人索要验证码均为诈骗，请勿泄露！
     * 【邮储银行】验证码：759846（序号8003），您向王洪江尾号0640账户转账1600.00元。任何人索要验证码均为诈骗，请勿泄露！
     * 手机交易码628123,您正向谢传东(9921)汇款2,200.00元,请确认通过中行官方渠道办理【中国银行】
     * 您的借记卡账户5051，于07月01日手机银行支取人民币2200.00元,交易后余额2261.60【中国银行】
     * 手机交易码540879,您正向杨霞军(1312)汇款1,026.00元,请确认通过中行官方渠道办理【中国银行】
     * 手机交易码540879,您正向杨霞军(1312)汇款1,026.00元,请确认通过中行官方渠道办理【中国银行】
     * 您的借记卡账户5051，于07月01日手机银行支取(跨行支付)人民币1026.00元,交易后余额1235.60【中国银行】
     * 您的借记卡账户5051，于07月01日手机银行支取(跨行支付)人民币1026.00元,交易后余额1235.60【中国银行】
     * 您的借记卡账户7990，于07月01日手机银行支取(跨行支付)人民币1902.00元,交易后余额4340.39【中国银行】
     * 您的借记卡账户7990，于07月01日手机银行支取(跨行支付)人民币1902.00元,交易后余额4340.39【中国银行】
     * 您的借记卡账户7990，于07月01日手机银行支取(跨行支付)人民币1902.00元,交易后余额4340.39【中国银行】
     * 您的借记卡账户5051，于07月01日手机银行支取(跨行支付)人民币2000.00元,交易后余额4461.60【中国银行】
     * 您的借记卡账户5051，于07月01日收入(网银跨行)人民币500.00元,交易后余额4809.64【中国银行】
     * 您的借记卡账户0211，于07月01日手机银行收入人民币1.00元,交易后余额4201.00【中国银行】
     * 您尾号*4733的卡于07月01日19:31手机银行跨行汇款1.00元,交易后余额为1.00元。【交通银行】
     * 您尾号9919的储蓄卡7月1日20时6分向张馨予电子汇出支出人民币1200.00元,活期余额4858.63元。[建设银行]
     * 【平安银行】您存款账户1241于7月2日11:32银联入账转入人民币12.00元，活期余额人民币43.70元。
     * 【平安银行】您存款账户1241于7月2日网银转账转出人民币1.00元收款人顾宇，活期余额人民币42.70元
     * 您尾号*9585的卡于07月02日12:32手机银行跨行汇款1.00元,交易后余额为8.00元。【交通银行】
     * 您尾号*9585的卡于07月02日12:33网银跨行汇款1.00元,交易后余额为9.00元。【交通银行】
     *
     * @param text
     * @return
     */

    /**
     * 转换日期
     *
     * @return
     */
    public static String getDate(String dateStr, DateDetail dateDetailFo) {
        if (dateStr.contains("/")) {
            return DatePattern.NORM_DATETIME_FORMAT.format(new Date());
        }

        DateDetail dateDetail = new DateDetail();
        Matcher matcher = Pattern.compile(SplitRegex.date.getFirstRegex()).matcher(dateStr);
        while (matcher.find()) {
            String group = matcher.group();
            Matcher matcher1 = Pattern.compile(SplitRegex.number.getFirstRegex()).matcher(group);
            if (group.contains("年")) {
                while (matcher1.find()) {
                    dateDetail.setYear("20" + matcher1.group());//设置年
                }
            }
            if (group.contains("月")) {
                while (matcher1.find()) {
                    dateDetail.setMonth(matcher1.group());//设置月
                }
            }

            if (group.contains("日")) {
                while (matcher1.find()) {
                    dateDetail.setDay(matcher1.group());//设置日
                }
            }
            if (group.contains("时")) {
                while (matcher1.find()) {
                    dateDetail.setHour(matcher1.group());
                }
            }
            if (group.contains("分")) {
                while (matcher1.find()) {
                    dateDetail.setMinute(matcher1.group());
                }
            }
            if (group.contains(":")) {
                String[] split = matcher.group().split(":");
                dateDetail.setHour(split[0]);
                dateDetail.setMinute(split[1]);
            }
        }
        getDefaultDate(dateDetailFo, dateStr);

        if (null != dateDetailFo&&StrUtil.isNotBlank(dateDetailFo.getHour())&&StrUtil.isNotBlank(dateDetailFo.getMinute())) {
            dateDetail = dateDetailFo;
        }

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));    //获取当前天数
        String time = String.valueOf(c.get(Calendar.HOUR_OF_DAY));       //获取当前小时
        String min = String.valueOf(c.get(Calendar.MINUTE));//获取当前分钟
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (time.length() == 1) {
            time = "0" + time;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }
        String format = String.format("2022-%s-%s %s:%s:00", dateDetail.getMonth() == null ? month : dateDetail.getMonth(),
                dateDetail.getDay() == null ?
                        day : dateDetail.getDay(), dateDetail.getHour() == null
                        ? time : dateDetail.getHour(), dateDetail.getMinute() == null ? min : dateDetail.getMinute());
        long time1 = StrToDate(format).getTime();
        Date date = new Date();
        if (dateDetailFo.getTransactionType().equals("expenditure") || dateDetailFo.getText().contains("河北农信")) {
            return format;
        }
        if (date.getTime() - time1 >= 5 * 60 * 1000 || date.getTime() < time1) {
            log.info("系统时间超过短信时间五分钟-系统时间-{},{}", DateToStr(date), format);
            throw new BusinessException("系统时间超过短信时间五分钟-系统时间-" + DateToStr(date) + ",短信时间-" + format);
        }
        return format;
    }


    public static void getDefaultDate(DateDetail detail, String dateStr) {
        try {
            String s = ReUtil.extractMulti("(\\d{4})-(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})", dateStr, "$1;$2;$3;$4;$5");
            String[] split = s.split(";");
            String year = StringUtils.equals(split[0], "@") ? null : split[0].trim();
            String month = StringUtils.equals(split[1], "@") ? null : split[1].trim();
            String day = StringUtils.equals(split[2], "@") ? null : split[2].trim();
            String hour = StringUtils.equals(split[3], "@") ? null : split[3].trim();
            String min = StringUtils.equals(split[4], "@") ? null : split[4].trim();
            detail.setYear(year);
            detail.setMonth(month);
            detail.setDay(day);
            detail.setHour(hour);
            detail.setMinute(min);
        } catch (Exception e) {
            log.error("默认时间未解析出来-{},{}", e.getMessage(), detail);
            return;
        }
    }

    public static void main(String[] args) {
        getDefaultDate(new DateDetail(),"2022-09-29 12:41:19");
    }



    public static String DateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
