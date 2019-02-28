/**
 *
 */
package com.fushun.framework.util.exception.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhoup
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext APP_CONTEXT;

    public static ApplicationContext getContext() {
        return APP_CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        APP_CONTEXT = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (APP_CONTEXT == null) {
            return null;
        }
        return APP_CONTEXT.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        if (APP_CONTEXT == null) {
            return null;
        }
        if (APP_CONTEXT.containsBean(beanName)) {
            return APP_CONTEXT.getBean(beanName);
        } else {
            return null;
        }
    }


}
