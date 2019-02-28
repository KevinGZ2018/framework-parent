package com.fushun.framework.util.util;

import java.lang.reflect.Field;

/**
 * 反射 工具类
 *
 * @author wangfushun
 * @version 1.0
 * @creation 2017年09月03日15时47分
 */
public class ReflectUtil {

    private ReflectUtil() {
    }

    public static Field getField(Class<?> cls, String fieldName) {

        try {
            if (cls == Object.class) {
                return null;
            }
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(cls.getSuperclass(), fieldName);
        }
    }
}