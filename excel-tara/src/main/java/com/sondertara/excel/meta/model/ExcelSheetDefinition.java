package com.sondertara.excel.meta.model;

import java.lang.annotation.Annotation;

/**
 * @author huangxiaohu
 */
public interface ExcelSheetDefinition {

    /**
     * 获取Sheet注解
     *
     * @param clazz
     * @return
     */
    <A extends Annotation> A getAnnotation(Class<A> clazz);


}
