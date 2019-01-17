package cn.kidtop.framework.util;

import cn.kidtop.framework.exception.BusinessException;

/**
 * 断言工具类
 *
 * @author zpcsa
 */
public abstract class AssertUtils {

    /**
     * 判断表达式为false抛错
     *
     * @param expression
     * @author zhoup
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, BusinessException.Enum.EXPRESSION_IS_FALSE_EXCEPTION);
    }

    /**
     * 判断表达式为false抛错
     *
     * @param expression
     * @param exception  验证枚举
     */
    public static void isTrue(boolean expression, BusinessException.Enum exception) {
        if (!expression) {
            throw new BusinessException(exception);
        }
    }

    /**
     * 判断表达式为Null抛错
     *
     * @param expression
     * @author zhoup
     */
    public static <T> void isNull(T expression) {
        if (BeanUtils.isEmpty(expression)) {
            throw new BusinessException(BusinessException.Enum.EXPRESSION_IS_NULL_EXCEPTION);
        }
    }

    /**
     * 判断表达式为Null抛错
     * 自定义错误
     *
     * @param expression
     * @param errorMsg
     * @date: 2017-11-02 13:52:38
     * @author:wangfushun
     * @version 1.0
     */
    public static <T> void isNull(T expression, String errorMsg) {
        if (BeanUtils.isEmpty(expression)) {
            throw new BusinessException(errorMsg, BusinessException.CustomizeMessageEnum.BASECODE_EXCEPTION);
        }
    }

    /**
     * 判断表达式不为Null抛错
     *
     * @param expression
     * @author zhoup
     */
    public static <T> void isNotNull(T expression) {
        if (BeanUtils.isEmpty(expression)) {
            throw new BusinessException(BusinessException.Enum.EXPRESSION_IS_NOT_NULL_EXCEPTION);
        }
    }

    /**
     * 判断表达式不等于0抛错
     *
     * @param expression
     * @author zhoup
     */
    public static <T> void isNotEqualToZero(Integer expression) {
        if (expression != 0) {
            throw new BusinessException(BusinessException.Enum.EXPRESSION_IS_NOT_EQUAL_ZERO_EXCEPTION);
        }
    }

    /**
     * 判断表达式等于0抛错
     *
     * @param expression
     * @author zhoup
     */
    public static <T> void isEqualToZero(Integer expression) {
        if (expression == 0) {
            throw new BusinessException(null, BusinessException.Enum.EXPRESSION_IS_EQUAL_ZERO_EXCEPTION);
        }
    }
}
