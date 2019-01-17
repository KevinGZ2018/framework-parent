package cn.kidtop.framework.util;

import cn.kidtop.framework.base.BaseEnum;
import cn.kidtop.framework.exception.ConverterException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class EnumUtil {

    private static final Map<String, Map<String, ?>> CACHED_ENUMS_MAP = new ConcurrentHashMap<String, Map<String, ?>>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends BaseEnum<F>, F> T getEnum(Class<T> clazz, F index) {
        T result = null;
        if (index == null) {
            return result;
        }
        synchronized (CACHED_ENUMS_MAP) {
            Map<String, ?> mapEnum = CACHED_ENUMS_MAP.get(clazz.getName());
            if (CollectionUtils.isEmpty(mapEnum)) {
                T[] enums = clazz.getEnumConstants();
                Map map = new HashMap();
                for (T iEnum : enums) {
                    if (iEnum instanceof BaseEnum) {
                        map.put(((BaseEnum) iEnum).getCode(), iEnum);
                    }
                }
                CACHED_ENUMS_MAP.put(clazz.getName(), map);
                result = (T) map.get(index);
            } else {
                result = (T) mapEnum.get(index);
            }
        }
        return result;
    }


    public static <T extends Enum<T>> T getEnum(T[] enums, int index) {
        Method m = null;
        for (T enum1 : enums) {
            Object obj = null;
            try {
                if (m == null) {
                    m = enum1.getClass().getMethod("value");
                }
                obj = m.invoke(enum1);
            } catch (Exception e) {
                throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
            }

            if (obj.equals(index)) {
                return (T) enum1;
            }
        }
        throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends BaseEnum<F>, F> T getEnumByText(Class<T> clazz, String text) {
        T result = null;
        if (text == null) {
            return result;
        }
        synchronized (CACHED_ENUMS_MAP) {
            Map<String, ?> mapEnum = CACHED_ENUMS_MAP.get(clazz.getName());
            if (CollectionUtils.isEmpty(mapEnum)) {
                T[] enums = clazz.getEnumConstants();
                Map map = new HashMap();
                for (T iEnum : enums) {
                    if (iEnum instanceof BaseEnum) {
                        map.put(((BaseEnum) iEnum).getText(), iEnum);
                    }
                }
                CACHED_ENUMS_MAP.put(clazz.getName(), map);
                result = (T) map.get(text);
            } else {
                result = (T) mapEnum.get(text);
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends BaseEnum<F>, F> T getEnumByCode(Class<T> clazz, int code) {
        T result = null;
        if (code == 0) {
            return result;
        }
        synchronized (CACHED_ENUMS_MAP) {
            Map<String, ?> mapEnum = CACHED_ENUMS_MAP.get(clazz.getName());
            if (CollectionUtils.isEmpty(mapEnum)) {
                T[] enums = clazz.getEnumConstants();
                Map map = new HashMap();
                for (T iEnum : enums) {
                    if (iEnum instanceof BaseEnum) {
                        map.put(((BaseEnum) iEnum).getCode(), iEnum);
                    }
                }
                CACHED_ENUMS_MAP.put(clazz.getName(), map);
                result = (T) map.get(code);
            } else {
                result = (T) mapEnum.get(code);
            }
        }
        return result;
    }

}
