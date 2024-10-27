package com.sondertara.common.reflect;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.text.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author jinuo.fang
 */
public class Modifiers {
    static final int MANDATED = 0x00008000;
    static final int SYNTHETIC = 0x00001000;

    private static final Set<Integer> supportedModifiers = new HashSet<Integer>(Lists.asList(Modifier.ABSTRACT,
            Modifier.FINAL,
            Modifier.INTERFACE,
            Modifier.NATIVE,
            Modifier.PUBLIC,
            Modifier.PRIVATE,
            Modifier.PROTECTED,
            Modifier.STATIC,
            Modifier.SYNCHRONIZED,
            Modifier.STRICT,
            Modifier.TRANSIENT,
            Modifier.VOLATILE,
            MANDATED,
            SYNTHETIC));

    static {
        Collection<Field> fields = ReflectUtils.getAllPublicFields(Modifier.class, true);
        fields = CollectionUtils.filter(fields, new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                return Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers());
            }
        });
        Collection<Integer> supportedModifiersInModifierClass = CollectionUtils.toCollect(fields, (Function<Field, Integer>) field -> {
            try {
                return ReflectUtils.getFieldValue(field, Modifier.class, false);
            } catch (Throwable ex) {
                return Modifier.PUBLIC;
            }
        });
        supportedModifiers.addAll(supportedModifiersInModifierClass);
    }

    public static boolean isSupportedModifier(int modifier) {
        return supportedModifiers.contains(modifier);
    }


    public static int modifiers(Member member) {
        return member.getModifiers();
    }


    public static boolean hasModifier(int modifiers, int modifier) {
        if (!isSupportedModifier(modifier)) {
            throw new IllegalArgumentException(StringUtils.format("Unsupported modifier: {}", modifier));
        }
        return (modifiers & modifier) != 0;
    }

    public static boolean hasModifier(Class clazz, int modifier) {
        return hasModifier(clazz.getModifiers(), modifier);
    }

    public static boolean hasModifier(Member member, int modifier) {
        return hasModifier(modifiers(member), modifier);
    }

    public static boolean isAbstract(Class clazz) {
        return hasModifier(clazz, Modifier.ABSTRACT);
    }

    public static boolean isAbstract(Member member) {
        return hasModifier(member, Modifier.ABSTRACT);
    }

    public static boolean isFinal(Class clazz) {
        return hasModifier(clazz, Modifier.FINAL);
    }

    public static boolean isFinal(Member member) {
        return hasModifier(member, Modifier.FINAL);
    }

    public static boolean isInterface(Class clazz) {
        return clazz.isInterface();
    }

    public static boolean isInterface(Member member) {
        return hasModifier(member, Modifier.INTERFACE);
    }

    public static boolean isNative(Class clazz) {
        return hasModifier(clazz, Modifier.NATIVE);
    }

    public static boolean isNative(Member member) {
        return hasModifier(member, Modifier.NATIVE);
    }

    public static boolean isPublic(Class clazz) {
        return hasModifier(clazz, Modifier.PUBLIC);
    }

    public static boolean isPublic(Member member) {
        return hasModifier(member, Modifier.PUBLIC);
    }

    public static boolean isPrivate(Class clazz) {
        return hasModifier(clazz, Modifier.PRIVATE);
    }

    public static boolean isPrivate(Member member) {
        return hasModifier(member, Modifier.PRIVATE);
    }

    public static boolean isProtected(Class clazz) {
        return hasModifier(clazz, Modifier.PROTECTED);
    }

    public static boolean isProtected(Member member) {
        return hasModifier(member, Modifier.PROTECTED);
    }

    public static boolean isStatic(Class clazz) {
        return hasModifier(clazz, Modifier.STATIC);
    }

    public static boolean isStatic(Member member) {
        return hasModifier(member, Modifier.STATIC);
    }

    public static boolean isSynchronized(Member member) {
        return hasModifier(member, Modifier.SYNCHRONIZED);
    }

    public static boolean isStrict(Member member) {
        return hasModifier(member, Modifier.STRICT);
    }

    public static boolean isTransient(Member member) {
        return hasModifier(member, Modifier.TRANSIENT);
    }

    public static boolean isVolatile(Member member) {
        return hasModifier(member, Modifier.VOLATILE);
    }

    public static boolean isMandated(int mod) {
        return (mod & MANDATED) != 0;
    }

    public static boolean isMandated(Class clazz) {
        return hasModifier(clazz, MANDATED);
    }

    public static boolean isMandated(Member member) {
        return hasModifier(member, MANDATED);
    }

    public static boolean isSynthetic(int mod) {
        return (mod & SYNTHETIC) != 0;
    }

    public static boolean isSynthetic(Class clazz) {
        return hasModifier(clazz, SYNTHETIC);
    }

    public static boolean isSynthetic(Member member) {
        return hasModifier(member, SYNTHETIC);
    }

    private Modifiers(){

    }
}
