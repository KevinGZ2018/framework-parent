package cn.kidtop.framework.elasticsearch.converter;

import cn.kidtop.framework.base.BaseCMP;
import cn.kidtop.framework.base.BeansContextUtil;
import cn.kidtop.framework.exception.ConverterException;
import cn.kidtop.framework.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CMPtoElastic转换工具类
 *
 * @author zhoup
 * @date 2016年8月14日
 */
@SuppressWarnings("rawtypes")
public final class ElasticSearchConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConverterUtil.class);
    private static final Map<String, ElasticSearchConverter> CACHED_CUSTOM_CONVERTER_MAP = new ConcurrentHashMap<String, ElasticSearchConverter>();
    private static final Map<String, EInvokeMethod> CACHED_INVOKE_METHOD = new HashMap<String, ElasticSearchConverterUtil.EInvokeMethod>();

    /**
     * 拷贝source中所有数据到XContentBuilder对象
     *
     * @param source
     * @return
     * @author zhoup
     */
    public static <T extends BaseCMP> Map<String, Object> convert(T source) {
        if (source == null) {
            return null;
        }

        Map<String, Object> xContentBuilder = new HashMap<String, Object>();

        // 直接返货所有字段转换
        copy(source, xContentBuilder);


        return xContentBuilder;
    }

    /**
     * 通过customConverterClass实现赋值source数据到target中 <br/>
     * <font color='red'>不拷贝source中的所有字段</font>
     *
     * @param source               源数据
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return
     */
    public static <T extends BaseCMP> Map<String, Object> convert(T source, Converter converter, Class<? extends ElasticSearchConverter> customConverterClass) {
        if (source == null) {
            return null;
        }

        Map<String, Object> xContentBuilder = new HashMap<String, Object>();

        // 直接返货所有字段转换
        copy(source, xContentBuilder);

        copy(source, customConverterClass, xContentBuilder);


        return xContentBuilder;
    }

    /**
     * 通过customConverterClass实现赋值source数据到target中 <br/>
     * <font color='red'>不拷贝source中的所有字段</font>
     *
     * @param source               源数据
     * @param customConverterClass 自定义实现转换类
     * @return
     */
    public static <T extends BaseCMP> Map<String, Object> convert(T source, Class<? extends ElasticSearchConverter> customConverterClass) {
        if (source == null) {
            return null;
        }
        Map<String, Object> xContentBuilder = new HashMap<String, Object>();


        copy(source, customConverterClass, xContentBuilder);


        return xContentBuilder;
    }

    /**
     * 直接copy字段
     *
     * @param source
     * @return
     * @author zhoup
     */
    private static <T extends BaseCMP> void copy(T source, Map<String, Object> xContentBuilder) {

        Field[] fields = source.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);

                //跳过特殊字段
                Annotation[] annotations = field.getAnnotations();
                boolean breakBoolean = false;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Transient) {
                        breakBoolean = true;
                        break;
                    }
                    if (annotation instanceof OneToMany) {
                        breakBoolean = true;
                        break;
                    }
                    if (annotation instanceof ManyToMany) {
                        breakBoolean = true;
                        break;
                    }
                    if (annotation instanceof ManyToOne) {
                        breakBoolean = true;
                        break;
                    }
                    if (annotation instanceof OneToOne) {
                        breakBoolean = true;
                        break;
                    }
                    //hibernate 依赖太大，需要其他处理方式
//					if(annotation instanceof ManyToAny){
//						breakBoolean=true;
//						break;
//					}
                }
                if (breakBoolean) {
                    continue;
                }

                String name = field.getName();

                if (name.equals("serialVersionUID")) {
                    continue;
                }
                Object object = field.get(source);

                if (!BeanUtils.isEmpty(object)) {
                    xContentBuilder.put(name, object);
                }
            }

            xContentBuilder.put("createdAt", source.getCreatedAt());
            xContentBuilder.put("updatedAt", source.getUpdatedAt());

        } catch (Exception e) {
            throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
        }
        return;
    }

    /**
     * 根据转换实现类  转换
     *
     * @param source
     * @param customConverterClass
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseCMP> void copy(T source, Class<? extends ElasticSearchConverter> customConverterClass, Map<String, Object> xContentBuilder) {
        ElasticSearchConverter customConverter = getCustomConverterInstance(customConverterClass);
        String key = source.getClass().getName();
        EInvokeMethod eInvokeMethod = CACHED_INVOKE_METHOD.get(key);
        if (eInvokeMethod == null) {
            copy(source, customConverter, xContentBuilder);
            return;
        }
        switch (eInvokeMethod) {
            case ConvertFromCMP:
                customConverter.convertFromCMP(source, xContentBuilder);
                break;
            default:
                break;
        }

    }

    /**
     * 执行转换，通过判断反射
     * 并且保存缓存
     *
     * @param source
     * @param customConverter
     * @param xContentBuilder
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月14日
     */
    @SuppressWarnings("unchecked")
    private static <T, F> void copy(T source, ElasticSearchConverter customConverter, Map<String, Object> xContentBuilder) {
        String key = source.getClass().getName();
        try {
            customConverter.convertFromCMP(source, xContentBuilder);
            CACHED_INVOKE_METHOD.put(key, EInvokeMethod.ConvertFromCMP);
            return;
        } catch (ClassCastException e) {
            logger.error(e.getMessage() + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
        }

        throw new ConverterException(null, ConverterException.Enum.CONVERTER_EXCEPTION);
    }

    private static <T> ElasticSearchConverter getCustomConverterInstance(
            Class<? extends ElasticSearchConverter> customConverterClass) {
        if (customConverterClass == null) {
            return null;
        }
        String key = customConverterClass.getName();
        ElasticSearchConverter converter = CACHED_CUSTOM_CONVERTER_MAP.get(key);
        if (converter == null) {
            synchronized (CACHED_CUSTOM_CONVERTER_MAP) {
                try {
                    converter = BeansContextUtil.getBean(customConverterClass);
                } catch (Exception e) {
                    logger.info(customConverterClass.getName() + " is not a component, need new instance.");
                }
                if (converter == null) {
                    try {
                        converter = customConverterClass.newInstance();
                        CACHED_CUSTOM_CONVERTER_MAP.put(key, converter);
                    } catch (InstantiationException e) {
                        throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
                    } catch (IllegalAccessException e) {
                        throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
                    }
                }
            }
        }
        return converter;
    }

    enum EInvokeMethod {
        ConvertFromCMP
    }

}
