package com.sondertara.common.equator;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;

/**
 * 不同的属性
 *
 * @author dadiyang
 */
public class DiffFieldInfo {
    @Setter
    @Getter
    private DifferType differType;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 第一个字段的类型
     */
    private Class<?> firstFieldType;
    /**
     * 第二个字段的类型
     */
    private Class<?> secondFieldType;
    /**
     * 第一个对象的值
     */
    private Object firstVal;
    /**
     * 第二个对象的值
     */
    private Object secondVal;

    public DiffFieldInfo() {
    }

    public DiffFieldInfo(DifferType differType, String fieldName, Class<?> firstFieldType, Class<?> secondFieldType) {
        this.fieldName = fieldName;
        this.differType = differType;
        this.firstFieldType = firstFieldType;
        this.secondFieldType = secondFieldType;
    }

    public DiffFieldInfo(String fieldName) {
        this.fieldName = fieldName;
    }


    public DiffFieldInfo(String fieldName, Class<?> firstFieldType, Class<?> secondFieldType) {
        this.fieldName = fieldName;
        this.firstFieldType = firstFieldType;
        this.secondFieldType = secondFieldType;
    }

    public DiffFieldInfo(DifferType differType, String fieldName, Class<?> fieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.differType = differType;
        this.firstFieldType = fieldType;
        this.secondFieldType = fieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public DiffFieldInfo(String fieldName, Class<?> fieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.firstFieldType = fieldType;
        this.secondFieldType = fieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public DiffFieldInfo(DifferType differType, String fieldName, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.differType = differType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public DiffFieldInfo(String fieldName, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public DiffFieldInfo(DifferType differType, String fieldName, Class<?> firstFieldType, Class<?> secondFieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.differType = differType;
        this.firstFieldType = firstFieldType;
        this.secondFieldType = secondFieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public DiffFieldInfo(String fieldName, Class<?> firstFieldType, Class<?> secondFieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.firstFieldType = firstFieldType;
        this.secondFieldType = secondFieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getFirstFieldType() {
        return firstFieldType;
    }

    public void setFirstFieldType(Class<?> firstFieldType) {
        this.firstFieldType = firstFieldType;
    }

    public Object getFirstVal() {
        return firstVal;
    }

    public void setFirstVal(Object firstVal) {
        this.firstVal = firstVal;
    }

    public void setSecondFieldType(Class<?> secondFieldType) {
        this.secondFieldType = secondFieldType;
    }

    public Class<?> getSecondFieldType() {
        return secondFieldType;
    }

    public Object getSecondVal() {
        return secondVal;
    }

    public void setSecondVal(Object secondVal) {
        this.secondVal = secondVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiffFieldInfo diffFieldInfo = (DiffFieldInfo) o;
        return Objects.equals(fieldName, diffFieldInfo.fieldName) &&
                Objects.equals(differType, diffFieldInfo.differType) &&
                Objects.equals(firstFieldType, diffFieldInfo.firstFieldType) &&
                Objects.equals(secondFieldType, diffFieldInfo.secondFieldType) &&
                Objects.equals(firstVal, diffFieldInfo.firstVal) &&
                Objects.equals(secondVal, diffFieldInfo.secondVal);
    }

    public String getDiffTypeName() {
        return Optional.ofNullable(differType).map(DifferType::getName).orElse(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, firstFieldType, secondFieldType, firstVal, secondVal);
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "diffType='" + getDiffTypeName() + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", firstFieldType=" + firstFieldType +
                ", secondFieldType=" + secondFieldType +
                ", firstVal=" + firstVal +
                ", secondVal=" + secondVal +
                '}';
    }
}
