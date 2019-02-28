package com.fushun.framework.zookeeper.lock;

/**
 * 锁
 *
 * @author zhoup
 * @date 2016年6月17日
 */
public interface Lock {
    /**
     * 获得锁
     *
     * @param timeout
     * @return
     */
    boolean lock(long timeout);

    /**
     * 释放锁
     */
    void unlock();

    /**
     * 销毁锁
     */
    void destroy();
}
