package com.sondertara.dynamic.reflect;


import com.sondertara.dynamic.reflect.compiler.JavassistCompiler;

import java.lang.reflect.Modifier;


@SuppressWarnings("rawtypes")
abstract public class ConstructorAccess<T> {
    boolean isNonStaticMemberClass;

    static public <T> ConstructorAccess<T> get(Class<T> type) {
        Class enclosingType = type.getEnclosingClass();
        boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

        String className = type.getName();
        String accessClassName = className + "ConstructorAccess";
        if (accessClassName.startsWith("java.")) {
            accessClassName = "reflectasm." + accessClassName;
        }

        Class accessClass = getAccessClass(accessClassName, className, isNonStaticMemberClass);

        try {
            ConstructorAccess access = (ConstructorAccess) accessClass.newInstance();
            access.isNonStaticMemberClass = isNonStaticMemberClass;
            return access;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class getAccessClass(String accessClassName, String className, boolean isNonStaticMemberClass) {
        String accessClassSimpleName = accessClassName.substring(accessClassName.lastIndexOf(".") + 1);
        String pkg = accessClassName.substring(0, accessClassName.lastIndexOf("."));
        StringBuffer sb = new StringBuffer("package ").append(pkg).append(";\n");
        sb.append("public class ").append(accessClassSimpleName).append(" extends com.sondertara.dynamic.reflect.ConstructorAccess {\n");
        sb.append("public Object newInstance() {\n");
        sb.append("return new ").append(className).append("();\n");
        sb.append("}\n");

        sb.append("public Object newInstance(Object enclosingInstance) {\n");

        if (isNonStaticMemberClass) {
            sb.append("throw new UnsupportedOperationException(\"Not an inner class.\");\n");
        } else {
            sb.append("throw new UnsupportedOperationException(\"Not an inner class.\");\n");
        }
        sb.append("}\n");
        sb.append("}\n");

        return JavassistCompiler.instance().compile(sb.toString(), AccessClassLoader.instance());
    }

    public boolean isNonStaticMemberClass() {
        return isNonStaticMemberClass;
    }

    /**
     * Constructor for top-level classes and static nested classes.
     * <p>
     * If the underlying class is a inner (non-static nested) class, a new instance will be created using <code>null</code> as the
     * this$0 synthetic reference. The instantiated object will work as long as it actually don't use any member variable or method
     * fron the enclosing instance.
     */
    abstract public T newInstance();

    /**
     * Constructor for inner classes (non-static nested classes).
     *
     * @param enclosingInstance The instance of the enclosing type to which this inner instance is related to (assigned to its
     *                          synthetic this$0 field).
     */
    abstract public T newInstance(Object enclosingInstance);

}
