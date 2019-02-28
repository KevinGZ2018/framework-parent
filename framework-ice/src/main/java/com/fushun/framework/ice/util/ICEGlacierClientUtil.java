package com.fushun.framework.ice.util;

import Ice.InitializationData;
import Ice.ObjectPrx;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ICEGlacierClientUtil {
    private static final String routerKey = "--Ice.Default.Router";
    private static final String ICE_DEFAULT_PACKAGE = "--Ice.Default.Package";
    private static volatile Ice.Communicator ic = null;
    @SuppressWarnings("rawtypes")
    private static Map<Class, ObjectPrx> cls2PrxMap = new HashMap<Class, ObjectPrx>();
    private static volatile long lastAccessTimestamp;
    private static volatile MonitorThread nonitorThread;
    private static long idleTimeOutSeconds = 0;
    private static String iceRouter = null;
    private static String iceDefaultPackage = null;

    public static Ice.Communicator getICECommunictor() {
        if (ic == null) {
            synchronized (ICEGlacierClientUtil.class) {
                if (ic == null) {
                    if (iceRouter == null) {
                        ResourceBundle rb = ResourceBundle.getBundle("iceclientGlacier2", Locale.ENGLISH);

                        iceRouter = rb.getString(routerKey);
                        iceDefaultPackage = rb.getString(ICE_DEFAULT_PACKAGE);
                        idleTimeOutSeconds = Integer.parseInt(rb.getString("idleTimeOutSeconds"));
                        System.out.println("Ice client's locator is " + routerKey + " proxy cache time out seconds :"
                                + idleTimeOutSeconds);
                    }
                    String[] initParams = new String[]{routerKey + "=" + iceRouter,
                            ICE_DEFAULT_PACKAGE + "=" + iceDefaultPackage,
                            "--Ice.Package.common=cn.kidtop.generated.ice.v1",
                            "--Ice.Package.shoppingcart=cn.kidtop.generated.ice.v1",
                            "--Ice.ThreadPool.Client.Size=1", "--Ice.Trace.Network=3",
                            "--Ice.ThreadPool.Client.SizeMax=50"};
                    // , "--Ice.Default.PreferSecure=1"
                    // String[] initParams = new String[] { locatorKey + "="
                    // + iceLocator };

                    ic = Ice.Util.initialize(initParams);

                    // 开启守护线程
                    createMonitorThread();
                }
            }
        }
        lastAccessTimestamp = System.currentTimeMillis();
        return ic;
    }

    private static void createMonitorThread() {
        nonitorThread = new MonitorThread();
        // 设定 daemonThread 为 守护线程，default false(非守护线程)
        nonitorThread.setDaemon(true);
        nonitorThread.start();
    }

    public static void closeCommunicator(boolean removeServiceCache) {
        synchronized (ICEGlacierClientUtil.class) {
            if (ic != null) {
                safeShutdown();
                nonitorThread.interrupt();
                if (removeServiceCache && !cls2PrxMap.isEmpty()) {
                    try {
                        cls2PrxMap.clear();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }

    }

    private static void safeShutdown() {
        try {
            ic.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ic.destroy();
            ic = null;
        }
    }

    /**
     * 仅限于Ice服务内部之间非异步方法的场景
     *
     * @param communicator
     * @param serviceCls
     * @return ObjectPrx
     */
    @SuppressWarnings("rawtypes")
    public static ObjectPrx getSerivcePrx(Ice.Communicator communicator, Class serviceCls) {
        return createIceProxy(communicator, serviceCls);

    }

    @SuppressWarnings("rawtypes")
    private static ObjectPrx createIceProxy(Ice.Communicator communicator, Class serviceCls) {
        ObjectPrx proxy = null;
        String clsName = serviceCls.getName();
        String serviceName = serviceCls.getSimpleName();
        int pos = serviceName.lastIndexOf("Prx");
        if (pos <= 0) {
            throw new java.lang.IllegalArgumentException("Invalid ObjectPrx class ,class name must end with Prx");
        }
        String realSvName = serviceName.substring(0, pos);


        Ice.RouterPrx defaultRouter = communicator.getDefaultRouter();
        Glacier2.RouterPrx router = Glacier2.RouterPrxHelper.checkedCast(defaultRouter);
        @SuppressWarnings("unused")
        Glacier2.SessionPrx session = null;
        try {
            session = router.createSession("zp", "123456");
        } catch (Glacier2.PermissionDeniedException ex) {

        } catch (Glacier2.CannotCreateSessionException ex) {

        }
        Ice.InitializationData initData = new InitializationData();
        initData.properties = ic.getProperties();
//		Ice.RouterFinder routerFinder = communicator.stringToProxy("");
//		
//		Ice.ObjectPrx prx = communicator.stringToProxy("Ice/RouterFinder:tcp -p 4063 -h prodhost");
//				Ice.RouterFinderPrx finder = Ice.RouterFinderPrxHelper.checkedCast(prx);
//				Ice::RouterPrx router = finder->getRouter();
//				communicator->setDefaultRouter(router);
        Glacier2.SessionHelper sessionHelper = new Glacier2.SessionHelper(null, initData, "Ice/RouterFinder:tcp -p 4063 -h 120.25.224.118", true);
        try {
            Ice.ObjectPrx base = sessionHelper.communicator().stringToProxy(realSvName);
            proxy = (ObjectPrx) Class.forName(clsName + "Helper").newInstance();
            Method m1 = proxy.getClass().getDeclaredMethod("uncheckedCast", ObjectPrx.class);
            proxy = (ObjectPrx) m1.invoke(proxy, base);
            return proxy;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 用于客户端API获取ICE服务实例的场景
     *
     * @param serviceCls
     * @return ObjectPrx
     */
    @SuppressWarnings("rawtypes")
    public static ObjectPrx getSerivcePrx(Class serviceCls) {
        ObjectPrx proxy = cls2PrxMap.get(serviceCls);
        if (proxy != null) {
            lastAccessTimestamp = System.currentTimeMillis();
            return proxy;
        }
        proxy = createIceProxy(getICECommunictor(), serviceCls);
        cls2PrxMap.put(serviceCls, proxy);
        lastAccessTimestamp = System.currentTimeMillis();
        return proxy;
    }

    // 守护线程
    static class MonitorThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000L);
                    if (lastAccessTimestamp + idleTimeOutSeconds * 1000L < System.currentTimeMillis()) {
                        closeCommunicator(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
