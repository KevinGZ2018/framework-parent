/**
 *
 */
package cn.kidtop.framework.spring;

import cn.kidtop.framework.base.BeansContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhoup
 */
@Component
public class SpringContextUtil extends BeansContextUtil implements ApplicationContextAware {

    private static ApplicationContext APP_CONTEXT;

    public static ApplicationContext getContext() {
        return APP_CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        APP_CONTEXT = applicationContext;
    }

    @Override
    protected <T> T getBeanByClass(Class<T> clazz) {
        return APP_CONTEXT.getBean(clazz);
    }

    @Override
    protected Object getBeanByName(String beanName) {
        if (APP_CONTEXT.containsBean(beanName)) {
            return APP_CONTEXT.getBean(beanName);
        } else {
            return null;
        }
    }

    @Override
    protected BeansContextUtil getBeansContextUtil() {
        return this;
    }
}
