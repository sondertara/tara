package com.sondertara.excel.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author huangxiaohu
 */
@Setter
@Getter
public class ExcelWriteSheetEntity implements Cloneable {

    private String sheetName;
    /**
     * excel属性
     */
    private List<ExcelCellEntity> propertyList;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}