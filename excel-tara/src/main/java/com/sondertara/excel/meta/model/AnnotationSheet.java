package com.sondertara.excel.meta.model;

import com.sondertara.common.text.StringUtils;
import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
@Getter
public class AnnotationSheet extends TaraSheet implements Comparable<AnnotationSheet> {
    protected Class<?> mappingClass;
    protected ExportFunction<?> queryFunction;

    private int lastColIndex;

    protected boolean useComplexHeader = false;

    protected int order = 0;
    protected ExcelDataType excelDataType;

    protected ExcelColBindType bindType = ExcelColBindType.DEF_ORDER;

    /**
     * columns : key is colIndex (0 based)
     */

    protected final Map<Integer, Field> colFields = new HashMap<>();

    public AnnotationSheet(Class<?> mappingClass) {
        super(0);
        this.mappingClass = mappingClass;
        ExcelComplexHeader complexHeader = getAnnotation(ExcelComplexHeader.class);
        if (null != complexHeader) {
            useComplexHeader = true;
        }
    }

    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return mappingClass.getAnnotation(clazz);
    }

    public void reConfigCol(ExcelRowDef row) {
        // not bind column by title
        if (!ExcelColBindType.TITLE.equals(this.bindType)) {
            return;
        }
        Map<String, Integer> titleMap = this.getTitles().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        this.getTitles().clear();
        HashMap<Integer, Field> fieldHashMap = new HashMap<>(this.colFields);
        this.colFields.clear();
        for (ExcelCellDef cell : row.getExcelCells()) {
            Integer colIndex = cell.getColIndex();
            String cellValue = cell.getCellValue();
            String title = StringUtils.trim(cellValue);
            Integer tmpIndex = titleMap.get(title);
            if (tmpIndex == null) {
                continue;
            }
            Field field = fieldHashMap.get(tmpIndex);
            this.getTitles().put(colIndex, title);
            this.colFields.put(colIndex, field);
            if (lastColIndex < colIndex) {
                lastColIndex = colIndex;
            }
        }
    }

    @Override
    public int compareTo(AnnotationSheet o) {
        return this.order - o.order;
    }


}
