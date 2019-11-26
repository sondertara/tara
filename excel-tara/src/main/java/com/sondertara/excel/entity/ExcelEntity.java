
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
    /**
     * 文件名
     */
    private String fileName;
    /**
     * excel属性
     */
    private List<ExcelPropertyEntity> propertyList;

    @Override
    public Object clone() {
        try {
            ExcelEntity entity = (ExcelEntity) super.clone();
            return entity;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}