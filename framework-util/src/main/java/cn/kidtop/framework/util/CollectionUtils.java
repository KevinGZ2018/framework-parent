package cn.kidtop.framework.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author wangfushun
 * @version 1.0
 * @creation 2018年12月16日23时31分
 */
public class CollectionUtils {

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Object[] objs) {
        return objs == null || objs.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return BeanUtils.isEmpty(collection) || collection.isEmpty();
    }
}
