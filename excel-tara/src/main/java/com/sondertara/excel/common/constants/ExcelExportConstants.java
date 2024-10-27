package com.sondertara.excel.common.constants;


import com.sondertara.common.concurrent.SafeConcurrentHashMap;
import com.sondertara.excel.support.converter.AbstractExcelColumnConverter;
import com.sondertara.excel.support.validator.AbstractExcelColumnValidator;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static com.sondertara.excel.utils.ColorUtils.EXCEL_GREEN_STRIPE;

/**
 * @author huangxiaohu
 */
public class ExcelExportConstants {

    public static final String SHEET_NAME = "Sheet";

    public static final  int MAX_PER_SHEET_COUNT=500_000;
    public static final  boolean ROW_STRIPED =false;
    public static final  boolean HAS_TITLE =true;
    public static final  boolean AUTO_WIDTH =false;
    public static final  String ROW_STRIPE_COLOR = EXCEL_GREEN_STRIPE;

    public static final int TITLE_ROW_HEIGHT =20;
    public static final int DATA_ROW_HEIGHT =20;
    public static final int DEFAULT_COL_WIDTH = 16;
    public static final int MAX_COL_WIDTH = 64;

    private static final Map<String, List<AbstractExcelColumnValidator<Annotation>>> columnValidatorCache = new SafeConcurrentHashMap<>();

    private static final Map<String, List<AbstractExcelColumnConverter<Annotation, ?>>> columnConverterCache = new SafeConcurrentHashMap<>();

    public static Map<String, List<AbstractExcelColumnValidator<Annotation>>> getColValidatorCache() {
        return columnValidatorCache;
    }

    public static Map<String, List<AbstractExcelColumnConverter<Annotation, ?>>> getColConverterCache() {
        return columnConverterCache;
    }

}
