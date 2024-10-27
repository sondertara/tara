package com.sondertara.common.reflect;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.reflect.parameter.ConstructorParameter;
import com.sondertara.common.reflect.parameter.MethodParameter;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ReflectUtilsTest {

    @Test
    void testGetConstructor() {
        // Setup

        // Run the test
        final Constructor<BigDecimal> result = ReflectUtils.getConstructor(BigDecimal.class, String.class);


        // Verify the results
        assertNotNull(result);
    }

    @Test
    void testGetConstructors() {

        // Run the test
        final Constructor<String>[] result = ReflectUtils.getConstructors(String.class);

        // Verify the results
//        assertArrayEquals(expectedResult, result);
    }


    @Test
    void testGetConstructorsDirectly() {
        // Setup
//        final Constructor<?>[] expectedResult = new Constructor<?>[]{};

        // Run the test
        final Constructor<?>[] result = ReflectUtils.getConstructorsDirectly(String.class);

        // Verify the results
//        assertArrayEquals(expectedResult, result);
    }

    @Test
    void testHasField() {
        assertTrue(ReflectUtils.hasField(ResultDTO.class, "success"));
    }

    @Test
    void testGetFieldName() throws NoSuchFieldException {

        // Setup
        final Field field = ClassUtils.getFields(ResultDTO.class).stream().filter(s -> s.getName().equals("success")).findFirst().get();

        // Run the test
        final String result = ReflectUtils.getFieldName(field);

        // Verify the results
        assertEquals("success", result);
    }

    @Test
    void testGetField() {

        // Run the test
        final Field result = ClassUtils.getField(ResultDTO.class, "success");

        // Verify the results
        assertEquals("success", result.getName());
    }


    @Test
    void testGetFieldMap() {


        // Run the test
        final Map<String, Field> result = ReflectUtils.getFieldMap(ResultDTO.class);

        // Verify the results
        assertEquals(4, result.size());
    }

    @Test
    void testGetFields1() {
        // Setup

        // Run the test
        final List<Field> result = ClassUtils.getFields(ResultDTO.class);

        // Verify the results
        assertEquals(4, result.size());
    }

    @Test
    void testGetFields2() {
        // Setup
        final Predicate<Field> fieldFilter = val -> {
            if ("success".equals(val.getName())) {
                return false;
            }
            return true;
        };

        // Run the test
        final List<Field> result = ClassUtils.getFields(ResultDTO.class, fieldFilter);

        // Verify the results
        assertEquals(3, result.size());
    }

    @Test
    void testGetFieldValue1() {
        ResultDTO<Object> resultDTO = ResultDTO.success();
        assertEquals(true, ClassUtils.getFieldValue(resultDTO, "success"));
    }


    @Test
    void testGetFieldsValue() {
        ResultDTO<String> resultDTO = ResultDTO.success("a");
        assertArrayEquals(new Object[]{""}, ReflectUtils.getFieldsValue(resultDTO));
    }

    @Test
    void testSetFieldValue1() {
        // Setup
        ResultDTO<String> resultDTO = ResultDTO.success("a");
        // Run the test
        ReflectUtils.setFieldValue(resultDTO, "success", false);

        // Verify the results
        assertFalse(resultDTO.getSuccess());
    }


    @Test
    void testIsOuterClassField() {
//        // Setup
//
//        // Run the test
//        final boolean result = ReflectUtils.isOuterClassField(field);
//
//        // Verify the results
//        assertFalse(result);
    }

    @Test
    void testGetPublicMethodNames() {
        ReflectUtils.getPublicMethodNames(ResultDTO.class);
    }


    @Test
    void testGetPublicMethod() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getPublicMethod(String.class, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetPublicMethod_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class,
                () -> ReflectUtils.getPublicMethod(String.class, "methodName", String.class));
    }

    @Test
    void testGetMethodOfObj() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodOfObj("obj", "methodName", "args");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodOfObj_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class, () -> ReflectUtils.getMethodOfObj("obj", "methodName", "args"));
    }

    @Test
    void testGetMethodIgnoreCase() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodIgnoreCase(String.class, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodIgnoreCase_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class,
                () -> ReflectUtils.getMethodIgnoreCase(String.class, "methodName", String.class));
    }

    @Test
    void testGetMethod1() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethod(String.class, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethod1_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class, () -> ReflectUtils.getMethod(String.class, "methodName", String.class));
    }

    @Test
    void testGetMethod2() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethod(String.class, false, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethod2_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class,
                () -> ReflectUtils.getMethod(String.class, false, "methodName", String.class));
    }

    @Test
    void testGetMethodByName1() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodByName(String.class, "methodName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodByName1_ThrowsSecurityException() {
        // Setup
        // Run the test
        assertThrows(SecurityException.class, () -> ReflectUtils.getMethodByName(String.class, "methodName"));
    }

    @Test
    void testGetMethodByNameIgnoreCase() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodByNameIgnoreCase(String.class, "methodName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodByName2() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodByName(String.class, false, "methodName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodNames() {
        assertEquals(new HashSet<>(Arrays.asList("value")), ReflectUtils.getMethodNames(String.class));
        assertEquals(Collections.emptySet(), ReflectUtils.getMethodNames(String.class));
        assertThrows(SecurityException.class, () -> ReflectUtils.getMethodNames(String.class));
    }

    @Test
    void testGetMethods1() {
        // Setup
        final Predicate<Method> filter = val -> {
            return false;
        };
        final Method[] expectedResult = new Method[]{};

        // Run the test
        final Method[] result = ReflectUtils.getMethods(String.class, filter);

        // Verify the results
        assertArrayEquals(expectedResult, result);
    }


    @Test
    void testGetMethods2() {// Setup
        final Method[] expectedResult = new Method[]{};

        // Run the test
        final Method[] result = ReflectUtils.getMethods(String.class);
    }


    @Test
    void testGetMethodsDirectly() {
        // Run the test
        final Method[] result = ReflectUtils.getMethodsDirectly(String.class, false, false);

    }


    @Test
    void testIsGetterOrSetter1() throws NoSuchMethodException {
        // Setup
        final Method method = ResultDTO.class.getMethod("getData");

        // Run the test
        final boolean result = ReflectUtils.isGetterOrSetter(method);

        // Verify the results
        assertTrue(result);
    }


    @Test
    void testNewInstance1() throws NoSuchMethodException {
        // Setup
        final Constructor<ResultDTO> constructor = ResultDTO.class.getConstructor();

        // Run the test
        ResultDTO resultDTO = ReflectUtils.newInstance(constructor);

        // Verify the results
        assertEquals(false, resultDTO.getSuccess());
    }


    @Test
    void testNewInstanceIfPossible() {
        ReflectUtils.newInstanceIfPossible(ResultDTO.class);
    }

    @Test
    void testInvokeStatic() {
        // Setup
        final Method method =MethodHandleUtils.getStaticMethods(ResultDTO.class).stream().findFirst().get();

        // Run the test
        final ResultDTO result = ReflectUtils.invokeStatic(method, "args");

        // Verify the results
        assertEquals("args", result.getMsg());
    }


    @Test
    void testInvoke1() throws NoSuchMethodException {
        // Setup
        ResultDTO<String> resultDTO = ResultDTO.success("result");
        final Method method = resultDTO.getClass().getMethod("getData");

        // Run the test
        final String result = ReflectUtils.invoke(resultDTO, method);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testInvoke2() {
        ResultDTO<String> resultDTO = ResultDTO.success("result");
        assertEquals("result", ReflectUtils.invoke(resultDTO, "getData" ));
    }

    @Test
    void testSetAccessible() throws NoSuchMethodException {
        // Setup
        final AccessibleObject accessibleObject =  ResultDTO.class.getMethod("getData");;

        // Run the test
        final AccessibleObject result = ReflectUtils.setAccessible(accessibleObject);

        // Verify the results
    }

    @Test
    void testLoadClassByName() throws Exception {
        assertEquals(String.class, ReflectUtils.loadClassByName("java.lang.String"));
        assertThrows(ClassNotFoundException.class, () -> ReflectUtils.loadClassByName("className"));
    }

    @Test
    void testGetOverriddenMethod() {
        // Setup
//        final Method method = null;
//        final Method expectedResult = null;
//
//        // Run the test
//        final Method result = ReflectUtils.getOverriddenMethod(method);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
    }

    @Test
    void testFindMethod1() {
//        // Setup
//        final Method methodToFind = null;
//        final Method expectedResult = null;
//
//        // Run the test
//        final Method result = ReflectUtils.findMethod(methodToFind, String.class);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
    }



    @Test
    void testGetDeclaredFields() {
        // Setup
        final List<Field> expectedResult = Arrays.asList();

        // Run the test
        final List<Field> result = ReflectUtils.getDeclaredFields(String.class);

        // Verify the results
    }





    @Test
    void testGetParameterAnnotations() {
        // Setup
//        final Method method = null;
//        final Annotation[][] expectedResult = new Annotation[][]{};
//
//        // Run the test
//        final Annotation[][] result = ReflectUtils.getParameterAnnotations(method);
//
//        // Verify the results
//        assertArrayEquals(expectedResult, result);
    }

    @Test
    void testSafeInvoke() throws NoSuchMethodException {
        // Setup
        ResultDTO<String> resultDTO = ResultDTO.success();
        Method[] methods = ReflectUtils.getMethods(resultDTO.getClass());


        final Method method = resultDTO.getClass().getMethod("setData", Object.class);

        // Run the test
        final Optional<Object> result = ReflectUtils.safeInvoke(method, resultDTO, "args");

        // Verify the results
    }

    @Test
    void testGetTypeName() {
        assertEquals("result", ReflectUtils.getTypeName(Object.class));
    }

    @Test
    void testIsInnerClass() {
        assertFalse(ReflectUtils.isInnerClass(String.class));
    }

    @Test
    void testIsLambda() {
        assertFalse(ReflectUtils.isLambda(String.class));
    }

    @Test
    void testIsStatic() {
        assertFalse(ReflectUtils.isStatic(String.class));
    }

    @Test
    void testIsAnonymousOrLocal() {
        assertFalse(ReflectUtils.isAnonymousOrLocal(String.class));
    }

    @Test
    void testIsAnonymous() {
        assertFalse(ReflectUtils.isAnonymous(Object.class));
    }

    @Test
    void testIsLocal() {
        assertFalse(ReflectUtils.isLocal(Object.class));
    }

    @Test
    void testMemberType() {
        // Setup
        final Member member = ResultDTO.class.getMethods()[0];

        // Run the test
        final Class<? extends Member> result = ReflectUtils.memberType(member);

        // Verify the results
        assertEquals(Method.class, result);
    }

    @Test
    void testGetSimpleClassName1() {
        assertEquals("result", ReflectUtils.getSimpleClassName("obj"));
    }

    @Test
    void testGetSimpleClassName2() {
        assertEquals("result", ReflectUtils.getSimpleClassName(Object.class));
    }

    @Test
    void testGetFQNClassName() {
        assertEquals("name", ReflectUtils.getFQNClassName(Object.class));
    }

    @Test
    void testGetPackageName1() {
        assertEquals("", ReflectUtils.getPackageName("classFullName"));
    }

    @Test
    void testGetPackageName2() {
        assertEquals("name", ReflectUtils.getPackageName(Object.class));
    }

    @Test
    void testGetJvmSignature() {
        assertEquals("result", ReflectUtils.getJvmSignature(Object.class));
    }

    @Test
    void testGetCodeLocationString() {
        assertEquals("url", ReflectUtils.getCodeLocationString(Object.class));
    }

    @Test
    void testGetCodeLocation() throws Exception {
        assertEquals(new URL("https://example.com/"), ReflectUtils.getCodeLocation(Object.class));
    }

    @Test
    void testHierarchy1() {
        assertEquals(Arrays.asList(String.class), ReflectUtils.hierarchy(String.class));
        assertEquals(Collections.emptyList(), ReflectUtils.hierarchy(String.class));
    }

    @Test
    void testHierarchy2() {
        assertEquals(Arrays.asList(String.class), ReflectUtils.hierarchy(String.class, false));
        assertEquals(Collections.emptyList(), ReflectUtils.hierarchy(String.class, false));
    }

    @Test
    void testIsCacheSafe() {
        // Setup
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Run the test
        final boolean result = ReflectUtils.isCacheSafe(String.class, classLoader);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsVisible() {
        // Setup
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Run the test
        final boolean result = ReflectUtils.isVisible(String.class, classLoader);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsAnnotationPresent() {
        // Setup
        final AnnotatedElement annotatedElement = null;

        // Run the test
        final boolean result = ReflectUtils.isAnnotationPresent(annotatedElement, Annotation.class);

        // Verify the results
        assertFalse(result);
    }


    @Test
    void testGetAnnotation3() {
        // Setup
        final AnnotatedElement annotatedElement = null;
        final Annotation expectedResult = null;

        // Run the test
        final Annotation result = ReflectUtils.getAnnotation(annotatedElement, Annotation.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetDeclaredAnnotation() {
        // Setup
        final AnnotatedElement annotatedElement = null;
        final Annotation expectedResult = null;

        // Run the test
        final Annotation result = ReflectUtils.getDeclaredAnnotation(annotatedElement, Annotation.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAnnotations() {
        // Setup
        final AnnotatedElement annotatedElement = null;
        final Annotation[] expectedResult = new Annotation[]{};

        // Run the test
        final Annotation[] result = ReflectUtils.getAnnotations(annotatedElement);

        // Verify the results
        assertArrayEquals(expectedResult, result);
    }

    @Test
    void testGetDeclaredAnnotations() {
        // Setup
        final AnnotatedElement annotatedElement = null;
        final List<Annotation> expectedResult = Arrays.asList();

        // Run the test
        final List<Annotation> result = ReflectUtils.getDeclaredAnnotations(annotatedElement);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetStaticField() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.getStaticField(Object.class, "fieldName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetPublicField() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.getPublicField(Object.class, "fieldName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetDeclaredField() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.getDeclaredField(Object.class, "fieldName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAnyField() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.getAnyField(Object.class, "fieldName");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindField1() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.findField(String.class, "name");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindField2() {
        // Setup
        final Field expectedResult = null;

        // Run the test
        final Field result = ReflectUtils.findField(String.class, "name", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAllFields1() {
        // Setup
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.findAllFields(String.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllDeclaredFields1() {
        // Setup
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.getAllDeclaredFields(String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllDeclaredFields2() {
        // Setup
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.getAllDeclaredFields(String.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllPublicInstanceFields() {
        // Setup
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.getAllPublicInstanceFields(String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllPublicFields() {
        // Setup
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.getAllPublicFields(String.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFilterFields() {
        // Setup
        final List<Field> fields = Arrays.asList();
        final Collection<Field> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Field> result = ReflectUtils.filterFields(fields, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetPublicFieldValueForcedIfPresent() {
        assertEquals("result", ReflectUtils.getPublicFieldValueForcedIfPresent("object", "field"));
    }

    @Test
    void testGetPublicFieldValue() {
        assertEquals("result", ReflectUtils.getPublicFieldValue("object", "field", false));
    }

    @Test
    void testGetDeclaredFieldValueForcedIfPresent() {
        assertEquals("result", ReflectUtils.getDeclaredFieldValueForcedIfPresent("object", "field"));
    }

    @Test
    void testGetDeclaredFieldValue() {
        assertEquals("result", ReflectUtils.getDeclaredFieldValue("object", "field", false));
    }

    @Test
    void testGetAnyFieldValueForcedIfPresent() {
        assertEquals("result", ReflectUtils.getAnyFieldValueForcedIfPresent("object", "field"));
    }

    @Test
    void testGetAnyFieldValue() {
        assertEquals("result", ReflectUtils.getAnyFieldValue("object", "field", false));
    }

    @Test
    void testGetFieldValue3() {
        // Setup
        final Field field = null;

        // Run the test
        final String result = ReflectUtils.getFieldValue(field, "object", false);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testSetPublicFieldValue() {
        // Setup
        // Run the test
        ReflectUtils.setPublicFieldValue("object", "field", "value", false);

        // Verify the results
    }

    @Test
    void testSetDeclaredFieldValue() {
        // Setup
        // Run the test
        ReflectUtils.setDeclaredFieldValue("object", "field", "value", false);

        // Verify the results
    }

    @Test
    void testSetAnyFieldValue() {
        // Setup
        // Run the test
        ReflectUtils.setAnyFieldValue("object", "field", "value", false);

        // Verify the results
    }

    @Test
    void testHasConstructor() {
        assertFalse(ReflectUtils.hasConstructor(String.class, Object.class));
    }

    @Test
    void testNewInstance3() {
        assertEquals("result", ReflectUtils.newInstance(String.class));
    }

    @Test
    void testNewInstance4() {
        assertEquals("result", ReflectUtils.newInstance(String.class, new Class[]{Object.class}, "parameters"));
    }

    @Test
    void testGetAnnotatedMethods() {
        // Setup
        final List<Method> expectedResult = Arrays.asList();

        // Run the test
        final List<Method> result = ReflectUtils.getAnnotatedMethods(String.class, Annotation.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindGetterOrSetter1() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.findGetterOrSetter(Object.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindGetterOrSetter2() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.findGetterOrSetter(Object.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindMethods1() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.findMethods(Object.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindMethods2() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.findMethods(Object.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindMethods3() {
        // Setup
        final Collection<Method> result1 = Arrays.asList();

        // Run the test
        ReflectUtils.findMethods(result1, Object.class, false);

        // Verify the results
    }

    @Test
    void testGetAllDeclaredMethods1() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.getAllDeclaredMethods(Object.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAllDeclaredMethods2() {
        // Setup
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.getAllDeclaredMethods(Object.class, false);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFilterMethods() {
        // Setup
        final Method[] methods = new Method[]{};
        final Collection<Method> expectedResult = Arrays.asList();

        // Run the test
        final Collection<Method> result = ReflectUtils.filterMethods(methods, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetDeclaredMethod() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getDeclaredMethod(Object.class, "methodName", Object.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetMethodIfAvailable() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getMethodIfAvailable(String.class, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetAnyMethod() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getAnyMethod(String.class, "methodName", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindMethod2() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.findMethod(String.class, "name", String.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testInvokePublicMethodForcedIfPresent() {
        assertEquals("result",
                ReflectUtils.invokePublicMethodForcedIfPresent("object", "methodName", new Class[]{Object.class},
                        new Object[]{"parameters"}));
    }

    @Test
    void testInvokePublicMethod() {
        assertEquals("result", ReflectUtils.invokePublicMethod("object", "methodName", new Class[]{Object.class},
                new Object[]{"parameters"}, false));
    }

    @Test
    void testInvokeDeclaredMethodForcedIfPresent() {
        assertEquals("result",
                ReflectUtils.invokeDeclaredMethodForcedIfPresent("object", "methodName", new Class[]{Object.class},
                        new Object[]{"parameters"}));
    }

    @Test
    void testInvokeDeclaredMethod() {
        assertEquals("result", ReflectUtils.invokeDeclaredMethod("object", "methodName", new Class[]{Object.class},
                new Object[]{"parameters"}, false));
    }

    @Test
    void testInvokeAnyMethodForcedIfPresent() {
        assertEquals("result",
                ReflectUtils.invokeAnyMethodForcedIfPresent("object", "methodName", new Class[]{Object.class},
                        new Object[]{"parameters"}));
    }

    @Test
    void testInvokeAnyMethod() {
        assertEquals("result", ReflectUtils.invokeAnyMethod("object", "methodName", new Class[]{Object.class},
                new Object[]{"parameters"}, false));
    }

    @Test
    void testInvokeMethod() {
        // Setup
        final Method method = null;

        // Run the test
        final String result = ReflectUtils.invokeMethod(method, "object", "parameters");

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testInvoke3() {
        // Setup
        final Method method = null;

        // Run the test
        final String result = ReflectUtils.invoke(method, "object", new Object[]{"parameters"}, false);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testInvokeGetterOrFiled() {
        assertEquals("result", ReflectUtils.invokeGetterOrFiled("object", "field", false));
    }

    @Test
    void testInvokeAnyStaticMethod1() throws Exception {
        assertEquals("result", ReflectUtils.invokeAnyStaticMethod("clazz", "methodName", new Class[]{Object.class},
                new Object[]{"parameters"}, false, false));
        assertThrows(ClassNotFoundException.class,
                () -> ReflectUtils.invokeAnyStaticMethod("clazz", "methodName", new Class[]{Object.class},
                        new Object[]{"parameters"}, false, false));
    }

    @Test
    void testInvokeAnyStaticMethod2() {
        assertEquals("result", ReflectUtils.invokeAnyStaticMethod(Object.class, "methodName", new Class[]{Object.class},
                new Object[]{"parameters"}, false, false));
    }

    @Test
    void testGetMethodString1() {
        assertEquals("result",
                ReflectUtils.getMethodString("clazzFQN", "methodName", Object.class, new Class[]{Object.class}));
    }



    @Test
    void testGetMethodParameter1() {
        // Setup
        final Method method = null;

        // Run the test
        final MethodParameter result = ReflectUtils.getMethodParameter("supplierName", method, 0);

        // Verify the results
    }

    @Test
    void testGetMethodParameter2() {
        // Setup
        final Method method = null;

        // Run the test
        final MethodParameter result = ReflectUtils.getMethodParameter(method, 0);

        // Verify the results
    }

    @Test
    void testGetMethodParameters1() {
        // Setup
        final Method method = null;

        // Run the test
        final List<MethodParameter> result = ReflectUtils.getMethodParameters("supplierName", method);

        // Verify the results
    }

    @Test
    void testGetMethodParameters2() {
        // Setup
        final Method method = null;

        // Run the test
        final List<MethodParameter> result = ReflectUtils.getMethodParameters(method);

        // Verify the results
    }

    @Test
    void testGetConstructorParameter1() {
        // Setup
        final Constructor constructor = null;

        // Run the test
        final ConstructorParameter result = ReflectUtils.getConstructorParameter("supplierName", constructor, 0);

        // Verify the results
    }

    @Test
    void testGetConstructorParameter2() {
        // Setup
        final Constructor constructor = null;

        // Run the test
        final ConstructorParameter result = ReflectUtils.getConstructorParameter(constructor, 0);

        // Verify the results
    }

    @Test
    void testGetConstructorParameters1() {
        // Setup
        final Constructor constructor = null;

        // Run the test
        final List<ConstructorParameter> result = ReflectUtils.getConstructorParameters(constructor);

        // Verify the results
    }

    @Test
    void testGetConstructorParameters2() {
        // Setup
        final Constructor constructor = null;

        // Run the test
        final List<ConstructorParameter> result = ReflectUtils.getConstructorParameters("supplierName", constructor);

        // Verify the results
    }

    @Test
    void testGetAllInterfaces2() {
        assertEquals(new HashSet<>(Arrays.asList(Object.class)), ReflectUtils.getAllInterfaces(Object.class));
        assertEquals(Collections.emptySet(), ReflectUtils.getAllInterfaces(Object.class));
    }

    @Test
    void testGetAllSuperClass() {
        assertEquals(new HashSet<>(Arrays.asList(Object.class)), ReflectUtils.getAllSuperClass(Object.class));
        assertEquals(Collections.emptySet(), ReflectUtils.getAllSuperClass(Object.class));
    }

    @Test
    void testGetSetter1() {
        assertEquals("result", ReflectUtils.getSetter("s"));
    }

    @Test
    void testGetGetter1() {
        assertEquals("result", ReflectUtils.getGetter("s"));
    }

    @Test
    void testGetIsGetter() {
        assertEquals("result", ReflectUtils.getIsGetter("s"));
    }

    @Test
    void testGetIsSetter() {
        assertEquals("result", ReflectUtils.getIsSetter("s"));
    }

    @Test
    void testGetSetter2() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getSetter(Object.class, "field");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetSetter3() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getSetter(Object.class, "field", Object.class);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testHasGetter() {
        // Setup
        final Field field = null;

        // Run the test
        final boolean result = ReflectUtils.hasGetter(field);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testHasSetter() {
        // Setup
        final Field field = null;

        // Run the test
        final boolean result = ReflectUtils.hasSetter(field);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testGetGetter2() {
        // Setup
        final Method expectedResult = null;

        // Run the test
        final Method result = ReflectUtils.getGetter(Object.class, "field");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testExtractFieldName1() {
        // Setup
        final Member member = null;

        // Run the test
        final String result = ReflectUtils.extractFieldName(member);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testExtractFieldName2() {
        // Setup
        final Method method = null;

        // Run the test
        final String result = ReflectUtils.extractFieldName(method);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testIsSetter() {
        // Setup
        final Method method = null;

        // Run the test
        final boolean result = ReflectUtils.isSetter(method);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsGetter() {
        // Setup
        final Method method = null;

        // Run the test
        final boolean result = ReflectUtils.isGetter(method);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testMakeAccessible1() {
        // Setup
        final Field field = null;

        // Run the test
        final boolean result = ReflectUtils.makeAccessible(field);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testMakeAccessible2() {
        // Setup
        final Method m = null;

        // Run the test
        final boolean result = ReflectUtils.makeAccessible(m);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testMakeAccessible3() {
        // Setup
        final Constructor c = null;

        // Run the test
        final boolean result = ReflectUtils.makeAccessible(c);

        // Verify the results
        assertFalse(result);
    }


    @Test
    void testIsHashCodeMethod() {
        // Setup
        final Method method = null;

        // Run the test
        final boolean result = ReflectUtils.isHashCodeMethod(method);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsToStringMethod() {
        // Setup
        final Method method = null;

        // Run the test
        final boolean result = ReflectUtils.isToStringMethod(method);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsObjectMethod() {
        // Setup
        final Method method = null;

        // Run the test
        final boolean result = ReflectUtils.isObjectMethod(method);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testIsOverrideEquals() {
        assertFalse(ReflectUtils.isOverrideEquals(Object.class));
    }

    @Test
    void testIsOverrideHashCode() {
        assertFalse(ReflectUtils.isOverrideHashCode(Object.class));
    }





    @Test
    void testGetComponentType() {
        assertEquals(String.class, ReflectUtils.getComponentType(new String[]{"array"}));
    }
}
