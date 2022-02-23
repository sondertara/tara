package com.sondertara.excel.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author huangxiaohu
 */
@Setter
@Getter
public class ExcelEntity implements Cloneable {

    private String sheetName;
    /**
     * excel属性
     */
    private List<ExcelPropertyEntity> propertyList;

    @Override
    public Object clone() {
        try {
            return (ExcelEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}