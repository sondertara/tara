package com.sondertara.dynamic.reflect;


import com.sondertara.dynamic.reflect.compiler.JavassistCompiler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangzhiyuan on 2018/8/13
 */
@SuppressWarnings("rawtypes")
public abstract class FieldAccess {
    protected static final ConcurrentHashMap<Class, FieldAccess> fieldAccessMap = new ConcurrentHashMap<>();
    private static final Map<String, String> primitiveMap = new HashMap<>();

    static {
        primitiveMap.put("boolean", "Boolean");
        primitiveMap.put("byte", "Byte");
        primitiveMap.put("char", "Character");
        primitiveMap.put("short", "Short");
        primitiveMap.put("int", "Integer");
        primitiveMap.put("long", "Long");
        primitiveMap.put("float", "Float");
        primitiveMap.put("double", "Double");
    }

    private String[] fieldNames;
    private Class[] fieldTypes;
    private Field[] fields;

    /**
     * @param type Must not be the Object class, an interface, a primitive type, or void.
     */
    static public FieldAccess get(Class type) {
        if (type.getSuperclass() == null) {
            throw new IllegalArgumentException("The type must not be the Object class, an interface, a primitive type, or void.");
        }

        if (fieldAccessMap.get(type) != null) {
            return fieldAccessMap.get(type);
        }

        ArrayList<Field> fields = new ArrayList<Field>();
        Class nextClass = type;
        while (nextClass != Object.class) {
            Field[] declaredFields = nextClass.getDeclaredFields();
            for (int i = 0, n = declaredFields.length; i < n; i++) {
                Field field = declaredFields[i];
//                int modifiers = field.getModifiers();
//                if (Modifier.isStatic(modifiers)) continue;
//                if (Modifier.isPrivate(modifiers)) continue;
                fields.add(field);
            }
            nextClass = nextClass.getSuperclass();
        }

        String[] fieldNames = new String[fields.size()];
        Class[] fieldTypes = new Class[fields.size()];
        for (int i = 0, n = fieldNames.length; i < n; i++) {
            fieldNames[i] = fields.get(i).getName();
            fieldTypes[i] = fields.get(i).getType();
        }

        String className = type.getName();
        String accessClassName = className + "FieldAccess";
        if (accessClassName.startsWith("java.")) {
            accessClassName = "dtReflect." + accessClassName;
        }

        Class accessClass = getAccessClass(accessClassName, className, fields);

        try {
            FieldAccess access = (FieldAccess) accessClass.newInstance();
            access.fieldNames = fieldNames;
            access.fieldTypes = fieldTypes;
            access.fields = fields.toArray(new Field[0]);
            return access;
        } catch (Throwable t) {
            throw new RuntimeException("Error constructing field access class: " + accessClassName, t);
        }
    }

    private static Class getAccessClass(String accessClassName, String className, ArrayList<Field> fields) {
        String accessClassSimpleName = accessClassName.substring(accessClassName.lastIndexOf(".") + 1);
        String pkg = accessClassName.substring(0, accessClassName.lastIndexOf("."));
        StringBuffer sb = new StringBuffer("package ").append(pkg).append(";\n");
        sb.append("public class ").append(accessClassSimpleName).append(" extends com.sondertara.dynamic.reflect.FieldAccess {\n");


        String[] typeList = new String[]{"Object", "String", "boolean", "byte", "short", "int", "long", "double", "float", "char"};
        for (String typeName : typeList) {
            insertGetter(sb, fields, typeName, className);
            insertSetter(sb, fields, typeName, className);
        }
        sb.append("}");

//        System.out.println(sb.toString());
        return JavassistCompiler.instance().compile(sb.toString(), AccessClassLoader.instance());

    }

    /**
     * 生成 setter 方法
     *
     * @param sb
     * @param fields
     * @param typeName  要设置的值value的类型
     * @param className 原class（需要生成 access 类）的类型
     */
    private static void insertSetter(StringBuffer sb, ArrayList<Field> fields, String typeName, String className) {
        String uppercaseType = typeName.substring(0, 1).toUpperCase().concat(typeName.substring(1));
        boolean isObject = "Object".equals(typeName);
        if (isObject) {
            uppercaseType = "";
        }
        sb.append("public void set").append(uppercaseType).append("(Object instance, int fieldIndex, ").append(typeName).append(" value) {\n");
        sb.append("switch(fieldIndex) {\n");
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldType = field.getType().getName();
            boolean isPrimitiveType = isPrimitiveType(fieldType);


            sb.append("case ").append(i).append(":\n");

            boolean typeMatch = isPrimitiveType ? fieldType.equals(typeName) : field.getType().getName().endsWith("." + typeName);
            if (!isObject && !typeMatch) {
                sb.append("throw new IllegalArgumentException(\"Field not declared as ").append(typeName).append(", fieldIndex: \" + fieldIndex + \", fieldName: ").append(field.getName()).append("\");\n");
                continue;
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                sb.append("setPrivate(instance, fieldIndex, value);\n");
                sb.append("return;\n");
                continue;
            }

            // javassist 不能处理基本类型的自动装箱与拆箱，需要特殊处理
            if (isObject && isPrimitiveType) {
                String uppercaseFieldType = fieldType.substring(0, 1).toUpperCase().concat(fieldType.substring(1));
                String wrapperType = getWrapperType(fieldType);
                sb.append("set").append(uppercaseFieldType).append("(instance, fieldIndex, ").append(wrapperType).append(".valueOf(value.toString()).").append(fieldType).append("Value());\n");
                sb.append("return;\n");
                continue;
            }

            sb.append("((").append(className).append(") instance).").append(field.getName()).append(" = (").append(field.getType().getName()).append(") value").append(";\n");
            sb.append("return;\n");
        }
        sb.append("default:\n");
        sb.append("throw new IllegalArgumentException(\"Field not found, fieldIndex: \" + fieldIndex);\n");
        sb.append("}\n");
        sb.append("}\n");
    }

    /**
     * @param sb
     * @param fields
     * @param typeName  要设置的值value的类型
     * @param className 原class（需要生成 access 类）的类型
     */
    private static void insertGetter(StringBuffer sb, ArrayList<Field> fields, String typeName, String className) {
        String uppercaseType = typeName.substring(0, 1).toUpperCase().concat(typeName.substring(1));
        boolean isObject = "Object".equals(typeName);
        if (isObject) {
            uppercaseType = "";
        }
        sb.append("public ").append(typeName).append(" get").append(uppercaseType).append(" (Object instance, int fieldIndex) {\n");
        sb.append("switch(fieldIndex) {\n");
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldType = field.getType().getName();
            boolean isPrimitiveType = isPrimitiveType(fieldType);

            sb.append("case ").append(i).append(":\n");
            boolean typeMatch = isPrimitiveType ? fieldType.equals(typeName) : field.getType().getName().endsWith("." + typeName);
            if (!isObject && !typeMatch) {
                sb.append("throw new IllegalArgumentException(\"Field not declared as ").append(typeName).append(", fieldIndex: \" + fieldIndex + \", fieldName: ").append(field.getName()).append("\");\n");
                continue;
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                sb.append("return (").append(fieldType).append(") getPrivate(instance, fieldIndex);\n");
                continue;
            }

            // javassist 不能处理基本类型的自动装箱与拆箱，需要特殊处理
            if (isObject && isPrimitiveType(fieldType)) {
                String wrapperType = getWrapperType(fieldType);
                sb.append("return ").append(wrapperType).append(".valueOf( ((").append(className).append(")instance).").append(field.getName()).append(");\n");
                continue;
            }

            sb.append("return ((").append(className).append(")instance).").append(field.getName()).append(";\n");
        }
        sb.append("default:\n");
        sb.append("throw new IllegalArgumentException(\"Field not found, fieldIndex: \" + fieldIndex);\n");
        sb.append("}\n");
        sb.append("}\n");
    }

    private static boolean isPrimitiveType(String typeName) {
        return primitiveMap.containsKey(typeName);
    }

    private static String getWrapperType(String typeName) {
        return primitiveMap.get(typeName);
    }

    protected Object getPrivate(Object instance, String fieldName) {
        int index = getIndex(fieldName);
        return getPrivate(instance, index);
    }

    protected Object getPrivate(Object instance, int fieldIndex) {
        Field field = fields[fieldIndex];
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Access field failed, field:" + field.getName(), e);
        }
    }

    protected void setPrivate(Object instance, int fieldIndex, Object value) {
        Field field = fields[fieldIndex];
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Set field failed, field:" + field.getName(), e);
        }
    }

    public int getIndex(String fieldName) {
        for (int i = 0, n = fieldNames.length; i < n; i++) {
            if (fieldNames[i].equals(fieldName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private field: " + fieldName);
    }

    public int getIndex(Field field) {
        for (int i = 0, n = fields.length; i < n; i++) {
            if (fields[i].equals(field)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private field: " + field);
    }

    public void set(Object instance, String fieldName, Object value) {
        set(instance, getIndex(fieldName), value);
    }

    public Object get(Object instance, String fieldName) {
        return get(instance, getIndex(fieldName));
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public Class[] getFieldTypes() {
        return fieldTypes;
    }

    public int getFieldCount() {
        return fieldTypes.length;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    abstract public void set(Object instance, int fieldIndex, Object value);

    abstract public void setBoolean(Object instance, int fieldIndex, boolean value);

    abstract public void setByte(Object instance, int fieldIndex, byte value);

    abstract public void setShort(Object instance, int fieldIndex, short value);

    abstract public void setInt(Object instance, int fieldIndex, int value);

    abstract public void setLong(Object instance, int fieldIndex, long value);

    abstract public void setDouble(Object instance, int fieldIndex, double value);

    abstract public void setFloat(Object instance, int fieldIndex, float value);

    abstract public void setChar(Object instance, int fieldIndex, char value);

    abstract public Object get(Object instance, int fieldIndex);

    abstract public String getString(Object instance, int fieldIndex);

    abstract public char getChar(Object instance, int fieldIndex);

    abstract public boolean getBoolean(Object instance, int fieldIndex);

    abstract public byte getByte(Object instance, int fieldIndex);

    abstract public short getShort(Object instance, int fieldIndex);

    abstract public int getInt(Object instance, int fieldIndex);

    abstract public long getLong(Object instance, int fieldIndex);

    abstract public double getDouble(Object instance, int fieldIndex);

    abstract public float getFloat(Object instance, int fieldIndex);

}
