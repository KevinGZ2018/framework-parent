package com.fushun.framework.ice.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
    private static ClassPathXmlApplicationContext context;

    public static synchronized ClassPathXmlApplicationContext getContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
            context.registerShutdownHook();
        }
        return context;
    }

    public static synchronized void shutdown() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

}
