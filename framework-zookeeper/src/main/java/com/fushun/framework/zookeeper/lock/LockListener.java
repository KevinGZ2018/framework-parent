package com.fushun.framework.zookeeper.lock;

import com.fushun.framework.zookeeper.ZkClient;
import com.fushun.framework.zookeeper.exception.ZkClientException;
import com.fushun.framework.zookeeper.listener.Listener;
import org.apache.zookeeper.Watcher;

import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * 锁监听
 *
 * @author zhoup
 * @date 2016年6月17日
 */
public class LockListener implements Listener {
    private String lockPath;
    private ZkClient client;
    private Map<String, Semaphore> waitLocks = new ConcurrentHashMap<String, Semaphore>();

    public LockListener(String lockPath, ZkClient client) {
        this.lockPath = lockPath;
        this.client = client;
    }

    @Override
    public void listen(String path, Watcher.Event.EventType eventType, byte[] data) throws ZkClientException,
            SocketException {
        List<String> locks = client.getChild(lockPath, false);
        String acqLock = null;
        for (Map.Entry<String, Semaphore> entry : waitLocks.entrySet()) {
            String lock = entry.getKey();
            if (check(lock, locks)) {
                acqLock = lock;
                entry.getValue().release();
            }
        }
        if (acqLock != null) {
            waitLocks.remove(acqLock);
        }
    }

    /**
     * 添加等待队列
     *
     * @param path
     * @param semaphore
     */
    public void addQueue(String path, Semaphore semaphore) {
        waitLocks.put(path, semaphore);
    }

    private boolean check(String seq, List<String> locks) {
        boolean isLock = true;
        for (String lock : locks) {
            Long lock_ = Long.parseLong(lock);
            Long seq_ = Long.parseLong(seq);
            if (seq_ > lock_) {
                isLock = false;
                continue;
            }
        }
        return isLock;
    }
}
