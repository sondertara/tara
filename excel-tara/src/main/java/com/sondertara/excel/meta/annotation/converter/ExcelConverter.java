package com.sondertara.excel.meta.annotation.converter;


import com.sondertara.excel.support.converter.AbstractExcelColumnConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcelConverter {

    Class<? extends AbstractExcelColumnConverter> convertBy();

}
