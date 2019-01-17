/*
 * Project Name: kmfex-platform
 * File Name: ConverterService.java
 * Class Name: ConverterService
 *
 * Copyright 2014 KMFEX Inc
 *
 * 
 *
 * http://www.kmfex.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kidtop.framework.beans;

import cn.kidtop.framework.base.BaseCMP;
import cn.kidtop.framework.base.BeansContextUtil;
import cn.kidtop.framework.exception.BusinessException;
import cn.kidtop.framework.exception.ConverterException;
import cn.kidtop.framework.util.BeanUtils;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author wenzc
 */
@SuppressWarnings("rawtypes")
public final class ConverterUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConverterUtil.class);
    private static final Map<String, BeanCopier> CACHED_COPIER_MAP = new ConcurrentHashMap<String, BeanCopier>();
    private static final Map<String, ObjectConverter> CACHED_CUSTOM_CONVERTER_MAP = new ConcurrentHashMap<String, ObjectConverter>();
    private static final Map<String, EInvokeMethod> CACHED_INVOKE_METHOD = new HashMap<String, ConverterUtil.EInvokeMethod>();

    /**
     * sourceList集合中所有的数据赋值到List&lttarget&gt集合类型
     *
     * @param source     源数据泛型类型
     * @param target     目标数据泛型类型
     * @param sourceList 源数据List集合
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version
     * @creation 2016年4月1日
     */
    public static <T, F> List<F> convertList(Class<T> source, Class<F> target, List<T> sourceList) {
        return convertList(source, target, sourceList, null, null);
    }

    /**
     * sourceList集合中所有的数据赋值到List&lttarget&gt集合类型
     * <br/>再通过customConverterClass赋值数据
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceList           源数据List集合
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    @SuppressWarnings("unchecked")
    public static <T, F> List<F> convertList(Class<T> source, Class<F> target, List<T> sourceList, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        List<F> targetList = new ArrayList();
        if (sourceList != null && sourceList.size() != 0) {

            for (T t : sourceList) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, converter, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * 通过customConverterClass实现赋值sourceList集合中的数据到List&lttarget&gt集合类型
     * <br/><font color='red'>不拷贝源数据Class中的所有字段</font>
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceList           源数据List集合
     * @param customConverterClass 自定义实现转换类
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    @SuppressWarnings("unchecked")
    public static <T, F> List<F> convertList(Class<T> source, Class<F> target, List<T> sourceList, Class<? extends ObjectConverter> customConverterClass) {
        List<F> targetList = new ArrayList();
        if (sourceList != null && sourceList.size() != 0) {

            for (T t : sourceList) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * sourceSet集合中所有的数据赋值到Set&lttarget&gt集合类型
     *
     * @param source    源数据泛型类型
     * @param target    目标数据泛型类型
     * @param sourceSet 源数据Set集合
     * @return 返回Set&lttarget&gt集合类型
     * @author fushun
     * @version
     * @creation 2016年4月1日
     */
    public static <T, F> Set<F> convertSet(Class<T> source, Class<F> target, Set<T> sourceSet) {
        return convertSet(source, target, sourceSet, null, null);
    }

    /**
     * sourceSet集合中所有的数据赋值到Set&lttarget&gt集合类型
     * <br/>再通过customConverterClass赋值数据
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceSet            源数据Set集合
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return 返回Set&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    @SuppressWarnings("unchecked")
    public static <T, F> Set<F> convertSet(Class<T> source, Class<F> target, Set<T> sourceSet, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        Set<F> targetList = new HashSet();
        if (sourceSet != null && sourceSet.size() != 0) {

            for (T t : sourceSet) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, converter, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * 通过customConverterClass实现赋值sourceList集合中的数据到Set&lttarget&gt集合类型
     * <br/><font color='red'>不拷贝源数据Class中的所有字段</font>
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceSet            源数据Set集合
     * @param customConverterClass 自定义实现转换类
     * @return 返回Set&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    public static <T, F> Set<F> convertSet(Class<T> source, Class<F> target, Set<T> sourceSet, Class<? extends ObjectConverter> customConverterClass) {
        Set<F> targetList = new HashSet<F>();
        if (sourceSet != null && sourceSet.size() != 0) {

            for (T t : sourceSet) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * sourceIterable集合中所有的数据赋值到List&lttarget&gt集合类型
     *
     * @param source         源数据泛型类型
     * @param target         目标数据泛型类型
     * @param sourceIterable 源数据Iterable集合
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version
     * @creation 2016年4月1日
     */
    public static <T, F> List<F> convertIterableToList(Class<T> source, Class<F> target, Iterable<T> sourceIterable) {
        return convertIterableToList(source, target, sourceIterable, null, null);
    }

    /**
     * sourceIterable集合中所有的数据赋值到List&lttarget&gt集合类型
     * <br/>再通过customConverterClass赋值数据
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceIterable       源数据Iterable集合
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    @SuppressWarnings("unchecked")
    public static <T, F> List<F> convertIterableToList(Class<T> source, Class<F> target, Iterable<T> sourceIterable, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        List<F> targetList = new ArrayList();
        if (sourceIterable != null) {

            for (T t : sourceIterable) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, converter, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * 通过customConverterClass实现赋值sourceIterable集合中的数据到Iterable&lttarget&gt集合类型
     * <br/><font color='red'>不拷贝源数据Class中的所有字段</font>
     *
     * @param source               源数据泛型类型
     * @param target               目标数据泛型类型
     * @param sourceIterable       源数据Iterable集合
     * @param customConverterClass 自定义实现转换类
     * @return 返回List&lttarget&gt集合类型
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    @SuppressWarnings("unchecked")
    public static <T, F> List<F> convertIterableToList(Class<T> source, Class<F> target, Iterable<T> sourceIterable, Class<? extends ObjectConverter> customConverterClass) {
        List<F> targetList = new ArrayList();
        if (sourceIterable != null) {

            for (T t : sourceIterable) {
                try {
                    F f = target.newInstance();
                    targetList.add(convert(t, f, customConverterClass));
                } catch (Throwable e) {
                    logger.error("When copy instance" + t, e);
                    throw new BusinessException(e, BusinessException.Enum.DATA_ERROR_EXCEPTION);
                }
            }
            return targetList;
        } else {
            return targetList;
        }

    }

    /**
     * 拷贝source中所有数据到target
     *
     * @param source 源数据
     * @param target 目标数据
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    public static <T, F> F convert(T source, F target) {
        return convert(source, target, null, null);
    }

    /**
     * 通过customConverterClass实现赋值source数据到target中
     * <br/><font color='red'>不拷贝source中的所有字段</font>
     *
     * @param source               源数据
     * @param target               目标数据
     * @param customConverterClass 自定义实现转换类
     * @return
     */
    public static <T, F> F convert(T source, F target, Class<? extends ObjectConverter> customConverterClass) {
        if (source == null || target == null) {
            return null;
        }
        if (customConverterClass == null) {
            return null;
        }
        copy(source, target, customConverterClass);
        return target;
    }

    /**
     * 拷贝source中所有数据到target,
     * <br/>再通过customConverterClass赋值数据
     *
     * @param source               源数据
     * @param target               目标数据
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    public static <T, F> F convert(T source, F target, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        if (source == null || target == null) {
            return null;
        }
        copy(source, target, converter, customConverterClass);
        return target;
    }

    /**
     * 拷贝source中所有数据到target
     *
     * @param source 源数据
     * @param target 目标数据
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    public static <T, F> F convertJPAEntity(T source, F target) {
        verificationTargetClass(target);
        Date date = ((BaseCMP) target).getCreatedAt();
        Date updateDate = ((BaseCMP) target).getUpdatedAt();
        convert(source, target, null, null);
        ((BaseCMP) target).setCreatedAt(date);
        ((BaseCMP) target).setUpdatedAt(updateDate);
        return target;
    }

    /**
     * 拷贝source中所有数据到target,
     * <br/>再通过customConverterClass赋值数据
     *
     * @param source               源数据
     * @param target               目标数据
     * @param converter            传入null
     * @param customConverterClass 自定义实现转换类
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月1日
     */
    public static <T, F> F convertJPAEntity(T source, F target, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        if (source == null || target == null) {
            return null;
        }

        verificationTargetClass(target);
        Date date = ((BaseCMP) target).getCreatedAt();//保存Entity创建时间

        copy(source, target, converter, customConverterClass);

        ((BaseCMP) target).setCreatedAt(date);//还原Entity的创建时间

        return target;
    }

    /**
     * 验证对象是否为JPA Entity
     *
     * @param target
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月9日
     */
    private static <F> void verificationTargetClass(F target) {
        if (BeanUtils.isEmpty(target)) {
            throw new BusinessException(BusinessException.Enum.PARAMS_EXCEPTION);
        }
//	    	if (!target.getClass().isAnnotationPresent(Entity.class)) {
//	    		throw new BusinessException("转换数据不是JpaEntity", null, BusinessException.PARAMS_ERROR);
//			}
        if (!(target instanceof BaseCMP)) {
            throw new BusinessException(null, BusinessException.Enum.PARAMS_EXCEPTION);
        }
    }

    /**
     * @param source
     * @param target
     * @param customConverterClass
     */
    @SuppressWarnings("unchecked")
    private static <T, F> void copy(T source, F target, Class<? extends ObjectConverter> customConverterClass) {
        ObjectConverter customConverter = getCustomConverterInstance(customConverterClass);
        String key = source.getClass().getName() + "#" + target.getClass().getName();
        EInvokeMethod eInvokeMethod = CACHED_INVOKE_METHOD.get(key);
        if (eInvokeMethod == null) {
            copy(source, target, customConverter);
            return;
        }
        switch (eInvokeMethod) {
            case ConvertFromDto:
                customConverter.convertFromDto(source, target);
                break;
            case ConvertToDto:
                customConverter.convertToDto(source, target);
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
     * @param target
     * @param customConverter
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月14日
     */
    @SuppressWarnings("unchecked")
    private static <T, F> void copy(T source, F target, ObjectConverter customConverter) {
        String key = source.getClass().getName() + "#" + target.getClass().getName();
        try {
            customConverter.convertFromDto(source, target);
            CACHED_INVOKE_METHOD.put(key, EInvokeMethod.ConvertFromDto);
            return;
        } catch (ClassCastException e) {
            logger.info(e.getMessage() + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
        }

        try {
            customConverter.convertToDto(source, target);
            CACHED_INVOKE_METHOD.put(key, EInvokeMethod.ConvertToDto);
            return;
        } catch (ClassCastException e) {
            logger.info(e.getMessage() + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ConverterException(e, ConverterException.Enum.CONVERTER_EXCEPTION);
        }
        throw new ConverterException(ConverterException.Enum.CONVERTER_EXCEPTION);
    }

    /**
     * Private methods
     */

    private static <T, F> void copy(T source, F target, Converter converter, Class<? extends ObjectConverter> customConverterClass) {
        BeanCopier beanCopier = getBeanCopierInstance(source, target.getClass(), converter);
        beanCopier.copy(source, target, converter);
        if (customConverterClass != null) {
            copy(source, target, customConverterClass);
        }
    }

    private static <T, F> BeanCopier getBeanCopierInstance(T source, Class<F> targetClass, Converter converter) {
        String key = source.getClass().getName() + "#" + targetClass.getName();
        BeanCopier beanCopier = CACHED_COPIER_MAP.get(key);
        if (beanCopier == null) {
            synchronized (CACHED_COPIER_MAP) {
                beanCopier = CACHED_COPIER_MAP.get(key);
                if (beanCopier == null) {
                    beanCopier = TypeAwareBeanCopier.instantiate(source.getClass(), targetClass, converter != null);
                    CACHED_COPIER_MAP.put(key, beanCopier);
                }
            }
        }
        return beanCopier;
    }

    private static <T, F> ObjectConverter getCustomConverterInstance(Class<? extends ObjectConverter> customConverterClass) {
        if (customConverterClass == null) {
            return null;
        }
        String key = customConverterClass.getName();
        ObjectConverter converter = CACHED_CUSTOM_CONVERTER_MAP.get(key);
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
                        throw new ConverterException(null, ConverterException.Enum.CONVERTER_EXCEPTION);
                    } catch (IllegalAccessException e) {
                        throw new ConverterException(null, ConverterException.Enum.CONVERTER_EXCEPTION);
                    }
                }
            }
        }
        return converter;
    }

    enum EInvokeMethod {
        ConvertFromDto,
        ConvertToDto,
    }
}
