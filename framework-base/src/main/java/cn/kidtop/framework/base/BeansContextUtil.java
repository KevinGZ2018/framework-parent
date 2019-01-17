package cn.kidtop.framework.base;

/**
 * 获取对象的 Bean 的接口实现  需要子项目单独实现一下
 *
 * @verson 1.0
 * @return a
 * @date: 2018年12月16日01时42分
 * @author:wangfushun
 */
public abstract class BeansContextUtil {

    private static BeansContextUtil beansContextUtil;

    private static boolean isRun = false;

    public BeansContextUtil() {
        isRun = true;
        beansContextUtil = this;
    }

    public BeansContextUtil(BeansContextUtil beansContextUtil) {
        BeansContextUtil.beansContextUtil = beansContextUtil;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (isRun == false) {
            return null;
        }
        return beansContextUtil.getBeansContextUtil().getBeanByClass(clazz);
    }

    public static Object getBean(String beanName) {
        if (isRun == false) {
            return null;
        }
        return beansContextUtil.getBeansContextUtil().getBeanByName(beanName);
    }

    /**
     * 根据Class 获取类实列
     *
     * @param clazz
     * @return
     * @date: 2018年12月17日22时54分
     * @author:wangfushun
     * @version 1.0
     */
    protected abstract <T> T getBeanByClass(Class<T> clazz);

    /**
     * 根据实列名称获取实列对象
     *
     * @return
     * @date: 2018年12月17日22时54分
     * @author:wangfushun
     * @version 1.0
     */
    protected abstract Object getBeanByName(String beanName);

    /**
     * 获取自己子类实实列
     *
     * @return
     */
    protected abstract BeansContextUtil getBeansContextUtil();

    public void setBeansContextUtil(BeansContextUtil beansContextUtil) {
        isRun = true;
        BeansContextUtil.beansContextUtil = this;
    }

}
