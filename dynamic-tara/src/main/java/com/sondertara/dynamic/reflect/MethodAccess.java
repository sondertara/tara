package com.sondertara.dynamic.reflect;


import com.sondertara.dynamic.reflect.compiler.JavassistCompiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public abstract class MethodAccess {
    // 缓存
    private static final ConcurrentHashMap<Class, MethodAccess> METHED_ACCESS_MAP = new ConcurrentHashMap<>();

    protected String[] methodNames;
    protected List<Method> methods;
    private Class[][] parameterTypes;
    private Class[] returnTypes;

    /**
     * Creates a new MethodAccess for the specified type.
     *
     * @param type Must not be the Object class, a primitive type, or void.
     */
    static public MethodAccess get(Class type) {
        if (METHED_ACCESS_MAP.get(type) != null) {
            return METHED_ACCESS_MAP.get(type);
        }

        boolean isInterface = type.isInterface();
        if (!isInterface && type.getSuperclass() == null) {
            throw new IllegalArgumentException("The type must not be the Object class, an interface, a primitive type, or void.");
        }

        ArrayList<Method> methods = new ArrayList<Method>();
        if (!isInterface) {
            Class nextClass = type;
            while (nextClass != Object.class) {
                addDeclaredMethodsToList(nextClass, methods);
                nextClass = nextClass.getSuperclass();
            }
        } else {
            recursiveAddInterfaceMethodsToList(type, methods);
        }

        int n = methods.size();
        String[] methodNames = new String[n];
        Class[][] parameterTypes = new Class[n][];
        Class[] returnTypes = new Class[n];
        for (int i = 0; i < n; i++) {
            Method method = methods.get(i);
            methodNames[i] = method.getName();
            parameterTypes[i] = method.getParameterTypes();
            returnTypes[i] = method.getReturnType();
        }

        String className = type.getName();
        String accessClassName = className + "MethodAccess";
        if (accessClassName.startsWith("java.")) {
            accessClassName = "reflectasm." + accessClassName;
        }

        Class methodAccessClass = getAccessClass(accessClassName, className, methods, parameterTypes);

        try {
            MethodAccess access = (MethodAccess) methodAccessClass.newInstance();
            // 对实例中的属性赋值
            access.methodNames = methodNames;
            access.methods = methods;
            access.parameterTypes = parameterTypes;
            access.returnTypes = returnTypes;
            METHED_ACCESS_MAP.put(type, access);
            return access;
        } catch (Exception e) {
            throw new RuntimeException("Error constructing method access class: " + accessClassName, e);
        }
    }

    /**
     * 获取accessClass
     *
     * @param accessClassName AccessClass的全限定名
     * @param originClassName
     * @param methods
     * @param parameterTypes
     * @return
     */
    private static Class getAccessClass(String accessClassName, String originClassName, ArrayList<Method> methods, Class[][] parameterTypes) {
        String accessClassSimpleName = accessClassName.substring(accessClassName.lastIndexOf(".") + 1);

        String pkg = accessClassName.substring(0, accessClassName.lastIndexOf("."));
        StringBuilder sb = new StringBuilder("package ").append(pkg).append(";\n");
        sb.append("public class ").append(accessClassSimpleName).append(" extends com.sondertara.dynamic.reflect.MethodAccess {\n");
        sb.append("public Object invoke(Object paramObject, int paramInt, Object[] paramArrayOfObject) {\n");
        sb.append(originClassName).append(" accessor = (").append(originClassName).append(") paramObject;\n");
        sb.append("switch (paramInt) {\n");
        for (int i = 0; i < methods.size(); i++) {
            sb.append("case ").append(i).append(":\n");
            Method method = methods.get(i);

            // 私有方法
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getReturnType() != Void.TYPE) {
                    sb.append("return invokePrivate(paramObject, paramInt, paramArrayOfObject);\n");
                } else {
                    sb.append("invokePrivate(paramObject, paramInt, paramArrayOfObject);\n");
                    sb.append("return null;\n");
                }

                continue;
            }

            // 非私有方法
            if (method.getReturnType() != Void.TYPE) {
                sb.append("return accessor.").append(getMehodCallSignature(method, parameterTypes[i])).append(";\n");
            } else {
                sb.append("accessor.").append(getMehodCallSignature(method, parameterTypes[i])).append(";\n");
                sb.append("return null;\n");
            }
        }
        sb.append("}\n");
        sb.append("throw new IllegalArgumentException(\"Method not found: \" + methodNames[paramInt]);\n");
        sb.append("}\n");
        sb.append("}");

//        System.out.println(sb.toString());
        return JavassistCompiler.instance().compile(sb.toString(), AccessClassLoader.instance());
    }

    private static String getMehodCallSignature(Method method, Class[] parameterTypes) {
        StringBuffer sb = new StringBuffer(method.getName()).append("(");
        String methodSignature = method.toString();
        methodSignature = methodSignature.substring(methodSignature.indexOf("("));
        if (methodSignature.contains("throws")) {
            methodSignature = methodSignature.substring(0, methodSignature.indexOf("throws")).trim();
        }

        // 方法没有参数
        if ("()".equals(methodSignature)) {
            return sb.append(")").toString();
        }

        String[] splits = methodSignature.split(",");

        for (int i = 0; i < splits.length; i++) {
            String type = splits[i];
            if (i == 0) {
                type = type.substring(1, type.length());
            }
            if (i == splits.length - 1) {
                type = type.substring(0, type.length() - 1);
            }
            sb.append("(").append(type).append(") paramArrayOfObject[").append(i).append("]");
            if (i == splits.length - 1) {
                sb.append(")");
            } else {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    static private void addDeclaredMethodsToList(Class type, ArrayList<Method> methods) {
        Method[] declaredMethods = type.getDeclaredMethods();
        for (int i = 0, n = declaredMethods.length; i < n; i++) {
            Method method = declaredMethods[i];
            methods.add(method);
        }
    }

    static private void recursiveAddInterfaceMethodsToList(Class interfaceType, ArrayList<Method> methods) {
        addDeclaredMethodsToList(interfaceType, methods);
        for (Class nextInterface : interfaceType.getInterfaces()) {
            recursiveAddInterfaceMethodsToList(nextInterface, methods);
        }
    }

    /**
     * 调用 private 方法。供子类调用
     *
     * @param object
     * @param methodIndex
     * @param args
     * @return
     */
    protected Object invokePrivate(Object object, int methodIndex, Object... args) {
        Method method = methods.get(methodIndex);
        method.setAccessible(true);
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new IllegalStateException("invoke method error:" + method.getName(), e);
        }
    }

    abstract public Object invoke(Object object, int methodIndex, Object... args);

    /**
     * Invokes the method with the specified name and the specified param types.
     */
    public Object invoke(Object object, String methodName, Class[] paramTypes, Object... args) {
        return invoke(object, getIndex(methodName, paramTypes), args);
    }

    /**
     * Invokes the first method with the specified name and the specified number of arguments.
     */
    public Object invoke(Object object, String methodName, Object... args) {
        return invoke(object, getIndex(methodName, args == null ? 0 : args.length), args);
    }

    /**
     * Returns the index of the first method with the specified name.
     */
    public int getIndex(String methodName) {
        for (int i = 0, n = methodNames.length; i < n; i++) {
            if (methodNames[i].equals(methodName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private method: " + methodName);
    }

    /**
     * Returns the index of the first method with the specified name and param types.
     */
    public int getIndex(String methodName, Class... paramTypes) {
        for (int i = 0, n = methodNames.length; i < n; i++) {
            if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, parameterTypes[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private method: " + methodName + " " + Arrays.toString(paramTypes));
    }

    /**
     * Returns the index of the first method with the specified name and the specified number of arguments.
     */
    public int getIndex(String methodName, int paramsCount) {
        for (int i = 0, n = methodNames.length; i < n; i++) {
            if (methodNames[i].equals(methodName) && parameterTypes[i].length == paramsCount) {
                return i;
            }
        }
        throw new IllegalArgumentException(
                "Unable to find non-private method: " + methodName + " with " + paramsCount + " params.");
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    public Class[][] getParameterTypes() {
        return parameterTypes;
    }

    public Class[] getReturnTypes() {
        return returnTypes;
    }
}
