package com.fushun.framework.zookeeper.listener;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 监听器回调线程工程类
 *
 * @author zhoup
 * @date 2016年6月17日
 */
public class ThreadProcessFactory implements ThreadFactory {
    private AtomicInteger count = new AtomicInteger(0);

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("zkClient-listener-process-" + count.incrementAndGet());
        return thread;
    }
}
