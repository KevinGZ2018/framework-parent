package cn.kidtop.framework.zookeeper.lock;

import cn.kidtop.framework.zookeeper.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 该锁为进程间的锁,该对象线程安全支持多线程调用
 *
 * @author zhoup
 * @date 2016年6月17日
 */
public class SimpleLock implements Lock {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleLock.class);
    private static SimpleLock simpleLock = new SimpleLock();
    private final ThreadLocal<String> currentLock = new ThreadLocal<String>();
    private ZkClient client;
    private String lockPath = "/zk/lock/";
    private String seqPath;
    private LockListener lockListener;
    private volatile boolean isInit = false;

    private SimpleLock() {
    }

    /**
     * 获取锁实例
     *
     * @return
     */
    public static SimpleLock getInstance() {
        return simpleLock;
    }

    /**
     * 初始化
     *
     * @param client
     * @param path
     */
    public void init(ZkClient client, String path) {
        if (isInit) {
            LOGGER.warn("Repeat init simpleLock.");
            return;
        }
        this.client = client;
        if (path != null) {
            if (path.indexOf("/") == 0) {
                this.lockPath = path;
            }
            if (path.lastIndexOf("/") != path.length() - 1) {
                this.seqPath = this.lockPath + "/1";
            } else {
                this.seqPath = this.lockPath + "1";
            }
        }
        if (!client.exists(lockPath)) {
            this.client.create(lockPath, CreateMode.PERSISTENT);
        }
        lockListener = new LockListener(this.lockPath, this.client);
        client.listenChild(this.lockPath, lockListener);
    }

    /**
     * 等待获得锁的时间
     *
     * @param timeout 0 或者 大于0的 毫秒数，当设置为0时，程序将一直等待，直到获取到锁，
     *                当设置大于0时，等待获得锁的最长时间为timeout的值
     * @return 是否获取到锁，当超时时返回false
     */
    public boolean lock(long timeout) {
        final Semaphore lockObj = new Semaphore(0);
        String newPath = this.client.create(this.seqPath, "".getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
        currentLock.set(newPath);
        String[] paths = newPath.split("/");
        final String seq = paths[paths.length - 1];
        lockListener.addQueue(seq, lockObj);
        try {
            boolean islock;
            if (timeout >= 1) {
                islock = lockObj.tryAcquire(timeout, TimeUnit.MILLISECONDS);
            } else {
                lockObj.acquire();
                islock = true;
            }
            return islock;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void unlock() {
        client.delete(currentLock.get());
    }

    /**
     * 销毁锁
     */
    public void destroy() {
        client.unlintenChild(this.lockPath);
    }
}
