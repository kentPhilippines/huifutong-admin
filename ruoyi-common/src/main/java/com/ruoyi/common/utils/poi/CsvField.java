package com.ruoyi.common.utils.poi;

import java.lang.annotation.*;

/**
 * Bean导出CSV选项注解
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvField {
    /**
     * 字段的标题
     * @return
     */
    String value() default "";

    /**
     * 是否忽略此字段
     * @return
     */
    boolean ignore() default false;

    /**
     * 转换器，按需生成结果
     * @return
     */
    Class<? extends CsvConvertHandler> using() default CsvConvertHandler.None.class;
}