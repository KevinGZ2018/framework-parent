package com.fushun.framework.util.util;

/**
 * @author wangfushun
 * @version 1.0
 * @creation 2018年12月16日00时56分
 */
public class StringUtils extends org.springframework.util.StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }
}
