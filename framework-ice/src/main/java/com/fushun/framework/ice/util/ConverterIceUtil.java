package com.fushun.framework.ice.util;

import com.fushun.framework.util.exception.exception.ConverterException;
import com.fushun.framework.util.util.BeanUtils;
import com.fushun.framework.util.util.CollectionUtils;
import com.fushun.framework.util.util.DateUtil;
import com.fushun.framework.util.util.ExceptionUtil;
import net.sf.cglib.core.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ice 转换 实现类
 * Created by Administrator on 2017/6/22.
 */
class ConverterIceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConverterIceUtil.class);

    /**
     * ice 转换 get 方法
     */
    private static final Map<String, Method> CACHED_SOURCE_GET_METHOD_MAP = new ConcurrentHashMap<String, Method>();
    /**
     * ice 转换  属性get,set方法
     */
    private static final Map<Class<?>, PropertyDescriptor[]> CACHED_SOURCE_GET_MAP = new ConcurrentHashMap<Class<?>, PropertyDescriptor[]>();
    /**
     * ice转换 属性
     */
    private static final Map<Class<?>, Field[]> CACHED_ICE_FIELD_MAP = new ConcurrentHashMap<Class<?>, Field[]>();


    /**
     * ice转化类
     *
     * @param source
     * @param target
     * @return
     */
    public static <T, F> F converterDtoToIce(T source, F target) {
        if (source == null) return null;

        /**
         * soure fieldName;
         */
        Map<Object, PropertyDescriptor> sourceFieldNames = new HashMap<Object, PropertyDescriptor>();
        //TODO 代码优化无限父级循环
        Field[] fields = null, superFields = null;
        //代码优化1缓存source的getter方法2缓存目标对象的fields字段

        PropertyDescriptor[] getters = null;
        getters = CACHED_SOURCE_GET_MAP.get(source.getClass());
        if (CollectionUtils.isEmpty(getters)) {
            synchronized (CACHED_SOURCE_GET_MAP) {
                getters = ReflectUtils.getBeanGetters(source.getClass());
                CACHED_SOURCE_GET_MAP.put(source.getClass(), getters);
            }
        }

        for (int i = 0; i < getters.length; i++) {
            sourceFieldNames.put(getters[i].getName(), getters[i]);
        }

        fields = CACHED_ICE_FIELD_MAP.get(target.getClass());

        if (CollectionUtils.isEmpty(fields)) {
            synchronized (CACHED_ICE_FIELD_MAP) {
                fields = target.getClass().getDeclaredFields();
                CACHED_ICE_FIELD_MAP.put(target.getClass(), fields);
            }
        }
        superFields = CACHED_ICE_FIELD_MAP.get(target.getClass().getSuperclass());
        if (CollectionUtils.isEmpty(superFields)) {
            synchronized (CACHED_ICE_FIELD_MAP) {
                Class<?> superTarget = getSuperClass(target.getClass());
                superFields = superTarget.getDeclaredFields();
                CACHED_ICE_FIELD_MAP.put(superTarget, superFields);

            }
        }

        for (Field targetField : fields) {
            if (BeanUtils.isNotEmpty(sourceFieldNames.get(targetField.getName()))) {
                Method m1 = getMethod(source, targetField, sourceFieldNames);

                m1.setAccessible(true);
                try {
                    Object obj = m1.invoke(source);
                    PropertyDescriptor getter = sourceFieldNames.get(targetField.getName());
                    String getType = getter.getPropertyType().toString(); // 得到此属性的类型
                    String setType = targetField.getType().toString();


                    if (getType.endsWith("int") || getType.endsWith(".Integer")) {
                        if (setType.endsWith("int") || setType.endsWith("Integer")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0);
                            } else {
                                if ((Integer) obj > 0) {
                                    setTargetValue(target, targetField, obj);
                                } else {
                                    setTargetValue(target, targetField, 0);
                                }
                            }

                        } else if (setType.endsWith("long") || setType.endsWith("Long")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0L);
                            } else {
                                if ((Integer) obj > 0) {
                                    setTargetValue(target, targetField, ((Integer) obj).longValue());
                                } else {
                                    setTargetValue(target, targetField, 0L);
                                }
                            }
                        } else if (setType.endsWith("String")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, "0");
                            } else {
                                if (BeanUtils.isEmpty(obj)) {
                                    setTargetValue(target, targetField, "0");
                                } else {
                                    setTargetValue(target, targetField, ((Integer) obj).toString());
                                }
                            }
                        }
                    } else if (getType.endsWith("BigInteger")) {
                        if (setType.endsWith("int") || setType.endsWith("Integer")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0);
                            } else {
                                if ((Integer) obj > 0) {
                                    setTargetValue(target, targetField, ((BigInteger) obj).intValue());
                                } else {
                                    setTargetValue(target, targetField, 0);
                                }
                            }

                        } else if (setType.endsWith("long") || setType.endsWith("Long")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0L);
                            } else {
                                if ((Integer) obj > 0) {
                                    setTargetValue(target, targetField, ((BigInteger) obj).longValue());
                                } else {
                                    setTargetValue(target, targetField, 0L);
                                }
                            }
                        } else if (setType.endsWith("String")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, "0");
                            } else {
                                if (BeanUtils.isEmpty(obj)) {
                                    setTargetValue(target, targetField, "0");
                                } else {
                                    setTargetValue(target, targetField, ((BigInteger) obj).toString());
                                }
                            }
                        }
                    } else if (getType.endsWith("BigDecimal")) {
                        if (setType.endsWith("int") || setType.endsWith("Integer")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0);
                            } else {
                                if (((BigDecimal) obj).compareTo(BigDecimal.ZERO) == 1) {
                                    setTargetValue(target, targetField, ((BigDecimal) obj).intValue());
                                } else {
                                    setTargetValue(target, targetField, 0);
                                }
                            }
                        } else if (setType.endsWith("long") || setType.endsWith("Long")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, 0L);
                            } else {
                                if (((BigDecimal) obj).compareTo(BigDecimal.ZERO) == 1) {
                                    setTargetValue(target, targetField, ((BigDecimal) obj).longValue());
                                } else {
                                    setTargetValue(target, targetField, 0L);
                                }
                            }
                        } else if (setType.endsWith("String")) {
                            if (BeanUtils.isEmpty(obj)) {
                                setTargetValue(target, targetField, "0");
                            } else {
                                if (((BigDecimal) obj).compareTo(BigDecimal.ZERO) == 1) {
                                    setTargetValue(target, targetField, ((BigDecimal) obj).toPlainString());

                                } else {
                                    setTargetValue(target, targetField, "0");
                                }
                            }
                        }
                    } else if (getType.endsWith("Date")) {
                        if (BeanUtils.isEmpty(obj)) {
                            setTargetValue(target, targetField, "");
                        } else {
                            setTargetValue(target, targetField, DateUtil.getDateStr((Date) obj, DateUtil.FORMAT_STR));
                        }
                    } else {
                        setTargetValue(target, targetField, obj);
                    }

                } catch (IllegalAccessException e) {
                    logger.debug(ExceptionUtil.getPrintStackTrace(e));
                } catch (IllegalArgumentException e) {
                    logger.debug(ExceptionUtil.getPrintStackTrace(e));
                } catch (InvocationTargetException e) {
                    logger.debug(ExceptionUtil.getPrintStackTrace(e));
                }
            }
        }
        //处理父类
        for (Field field : superFields) {
            if (BeanUtils.isNotEmpty(sourceFieldNames.get(field.getName())) && target.getClass().getSuperclass().getSimpleName().indexOf("BaseModule") > -1) {
                Method sourceMethod = getMethod(source, field, sourceFieldNames);

                sourceMethod.setAccessible(true);
                try {
                    Object obj = sourceMethod.invoke(source);
                    field.setAccessible(true);
                    if (field.getName().endsWith("createdAt") || field.getName().endsWith("updatedAt")) {
                        if (BeanUtils.isEmpty(obj)) {
                            setTargetValue(target, field, null);
                        } else {
                            setTargetValue(target, field, DateUtil.getDateStr((Date) obj, DateUtil.FORMAT_STR));
                        }
                    } else {
                        setTargetValue(target, field, obj);
                    }
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
                } catch (IllegalArgumentException e) {
                    throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
                } catch (InvocationTargetException e) {
                    throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
                }
            }
        }
        return target;
    }

    /**
     * 获取最父级的 类
     *
     * @param target
     * @param target
     * @return
     */
    private static Class<?> getSuperClass(Class<?> target) {
        Class<?> superTarget = target.getSuperclass();
        if (superTarget.getName().equals("cn.kidtop.generated.ice.v1.common.BaseModule2") || superTarget.getName().equals("cn.kidtop.generated.ice.v1.common.BaseModule")) {
            return superTarget;
        }
        if (superTarget.getName().equals("Ice.ObjectImpl")) {
            return superTarget;
        }
        return getSuperClass(superTarget);
    }

    /**
     * 获取 Method
     *
     * @param source
     * @param field
     * @param sourceFieldNames
     * @return
     */
    private static <T> Method getMethod(T source, Field field, Map<Object, PropertyDescriptor> sourceFieldNames) {
        Method m1 = null;
        String soucreGetMethodSign = "convert#" + source.getClass().getName() + field.getName();
        m1 = CACHED_SOURCE_GET_METHOD_MAP.get(soucreGetMethodSign);
        if (BeanUtils.isEmpty(m1)) {
            synchronized (CACHED_SOURCE_GET_METHOD_MAP) {
                PropertyDescriptor getter = sourceFieldNames.get(field.getName());
                m1 = getter.getReadMethod();
                CACHED_SOURCE_GET_METHOD_MAP.put(soucreGetMethodSign, m1);
            }
        }
        return m1;
    }

    /**
     * 设置值 ice option字段会出现 get、set方法，设置值，必须使用set方法
     *
     * @param targetField
     * @param value
     */
    public static <T> void setTargetValue(T target, Field targetField, Object value) {

        //如果不是可见的字段，则有get、set方法
        try {
            if (Modifier.isPublic(targetField.getModifiers()) == false) {
                String filedName = targetField.getName();
                char[] cs = filedName.toCharArray();
                cs[0] -= 32;
                filedName = "set" + String.valueOf(cs);

                Method targetMethod = null;
                targetMethod = target.getClass().getMethod(filedName, targetField.getType());
                targetMethod.setAccessible(true);
                targetMethod.invoke(target, value);
                targetMethod.setAccessible(false);
            } else {
                targetField.setAccessible(true);
                if (value != null) {
                    targetField.set(target, value);
                }
                targetField.setAccessible(false);
            }
        } catch (Exception e) {
            logger.error("ice转错误", e);
        }
    }

    /**
     * 检测对象是否为 ice对象
     *
     * @param target
     * @return
     */
    public static boolean checkIceModule(Class<?> target) {
        String packageStr = target.getName();
        if (packageStr.indexOf("cn.kidtop.generated.ice.v1") > -1) {
            return true;
        }
        return false;
    }

    /**
     * 转换给ice的
     */
    public void converterIceToCjlbIce() {

    }
}
