package cn.kidtop.framework.ice.util;

import Ice.ObjectPrx;
import cn.kidtop.framework.exception.BusinessException;
import cn.kidtop.framework.ice.init.Sl4jIceBoxTestServer;
import cn.kidtop.framework.util.JsonUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ICEClientUtil {

    private static final String locatorKey = "--Ice.Default.Locator";
    private static final String ICE_DEFAULT_PACKAGE = "--Ice.Default.Package";
    private static ICEClientUtil iceClientUtil = new ICEClientUtil();
    private static volatile Ice.Communicator ic = null;
    private static Map<Class<? extends Ice.ObjectPrx>, ? super Ice.ObjectPrx> cls2PrxMap = new HashMap<Class<? extends ObjectPrx>, Ice.ObjectPrx>();
    private static volatile MonitorThread nonitorThread;
    private static String nonitorThreadLock = "";
    private static long idleTimeOutSeconds = 0;
    private static String iceLocator = null;
    private static String iceDefaultPackage = null;

    /**
     * 获取  本机测试  ice 请求对象
     * <P> icebox方式
     * <p>使用本地cglb本地代理，转换 iceImpl实现类和icePrx代理类的方法调用
     *
     * @param serviceImplCls
     * @return
     * @date: 2017-11-08 18:32:06
     * @author:wangfushun
     * @version 1.0
     */
    public static <T> T getTestProxySerivcePrx(Class<T> serviceImplCls) {
        Class<? extends Ice.ObjectPrx> cls2 = getTestProxySerivcePrxClass(serviceImplCls);
        IceCglibProxy iceCglibProxy = iceClientUtil.new IceCglibProxy();
        T t = iceCglibProxy.getProxy(serviceImplCls, cls2, true);
        return t;
    }

    /**
     * 获取  ice 请求对象
     * <P> icebox方式
     * <p>使用本地cglb本地代理，转换 iceImpl实现类和icePrx代理类的方法调用
     *
     * @param serviceImplCls
     * @return
     * @date: 2017-11-08 18:32:06
     * @author:wangfushun
     * @version 1.0
     */
    public static <T> T getProxySerivcePrx(Class<T> serviceImplCls) {
        Class<? extends Ice.ObjectPrx> cls2 = getTestProxySerivcePrxClass(serviceImplCls);
        IceCglibProxy iceCglibProxy = iceClientUtil.new IceCglibProxy();
        T t = iceCglibProxy.getProxy(serviceImplCls, cls2, false);
        return t;
    }

    /**
     * 获取ice 连接
     *
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    private static Ice.Communicator getICECommunictor() {
        if (ic == null) {
            synchronized (ICEClientUtil.class) {
                if (ic == null) {
                    if (iceLocator == null) {
                        ResourceBundle rb = ResourceBundle.getBundle(
                                "iceclient", Locale.ENGLISH);

                        iceLocator = rb.getString(locatorKey);
                        iceDefaultPackage = rb.getString(ICE_DEFAULT_PACKAGE);
                        idleTimeOutSeconds = Integer.parseInt(rb
                                .getString("idleTimeOutSeconds"));
                        System.out.println("Ice client's locator is "
                                + iceLocator
                                + " proxy cache time out seconds :"
                                + idleTimeOutSeconds);
                    }
                    String[] initParams = new String[]{locatorKey + "="
                            + iceLocator, ICE_DEFAULT_PACKAGE + "=" + iceDefaultPackage
                            , "--Ice.Package.common=cn.kidtop.generated.ice.v1"
                            , "--Ice.Package.shoppingcart=cn.kidtop.generated.ice.v1"
                            , "--Ice.Default.EndpointSelection=Ordered"
                            , "--Ice.ThreadPool.Client.Size=1"
                            , "--Ice.Trace.Network=3"
                            , "--Ice.ThreadPool.Client.SizeMax=50"};
                    // , "--Ice.Default.PreferSecure=1"
                    // String[] initParams = new String[] { locatorKey + "="
                    // + iceLocator };

                    ic = Ice.Util.initialize(initParams);
                    //开启守护线程
                    createMonitorThread();
                }
            }
        }
        return ic;
    }

    /**
     * 创建守护线程
     *
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    private static void createMonitorThread() {
        nonitorThread = new MonitorThread();
        // 设定 daemonThread 为 守护线程，default false(非守护线程)
        nonitorThread.setDaemon(true);
        nonitorThread.start();
    }

    /**
     * 程序主动销毁ice 连接
     *
     * @param removeServiceCache
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    public static void closeCommunicator(boolean removeServiceCache) {
        synchronized (cls2PrxMap) {
            System.out.println("销毁开始" + System.currentTimeMillis());
            if (ic != null) {
                closeIce(true);
            }
            System.out.println("销毁结束" + System.currentTimeMillis());
        }

    }

    /**
     * 关闭ice
     *
     * @param removeServiceCache
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    private static void closeIce(boolean removeServiceCache) {
        safeShutdown();
        if (removeServiceCache && !cls2PrxMap.isEmpty()) {
            try {
                cls2PrxMap.clear();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 关闭ice 远程连接对象
     *
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
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
     * 测试环境 获取代理对象
     *
     * @param communicator
     * @param serviceCls
     * @return
     * @date: 2017-11-08 17:58:24
     * @author:wangfushun
     * @version 1.0
     */
    @SuppressWarnings("unchecked")
    private static <T extends ObjectPrx> T getTestSerivcePrx(Class<T> serviceCls) {
        synchronized (ICEClientUtil.class) {
//			System.out.println("获取代理开始"+System.currentTimeMillis());
            T proxy = (T) cls2PrxMap.get(serviceCls);
            if (proxy != null) {
//				System.out.println("获取代理结束"+System.currentTimeMillis());
                return proxy;
            }
            proxy = (T) createIceProxy(getICECommunictor(), serviceCls, true);
            cls2PrxMap.put(serviceCls, proxy);
//			System.out.println("获取代理结束"+System.currentTimeMillis());
            return proxy;
        }
    }

    /**
     * 用于客户端API获取ICE服务实例的场景
     *
     * @param serviceCls
     * @return ObjectPrx
     */
    @SuppressWarnings({"unchecked"})
    private static <T extends Ice.ObjectPrx> T getSerivcePrx(Class<T> serviceCls) {
        synchronized (ICEClientUtil.class) {
//			System.out.println("获取代理开始"+System.currentTimeMillis());
            T proxy = (T) cls2PrxMap.get(serviceCls);
            if (proxy != null) {
//				System.out.println("获取代理结束"+System.currentTimeMillis());
                return proxy;
            }
            proxy = (T) createIceProxy(getICECommunictor(), serviceCls, false);
            cls2PrxMap.put(serviceCls, proxy);
//			System.out.println("获取代理结束"+System.currentTimeMillis());
            return proxy;
        }
    }


    /**
     * 获取ice实现类对应的Ice.ObjctPrx类Class
     *
     * @param serviceImplCls
     * @return
     * @date: 2017-11-13 16:43:29
     * @author:wangfushun
     * @version 1.0
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<? extends ObjectPrx> getTestProxySerivcePrxClass(Class<T> serviceImplCls) {
        Class<?>[] clss = serviceImplCls.getInterfaces();
        Class<?> cls = null;
        for (Class<?> class1 : clss) {
            if (!(class1 == Serializable.class)) {
                cls = class1;
            }
        }
        if (cls == null) {
            throw new BusinessException(null, BusinessException.Enum.BASECODE_EXCEPTION);
        }
        String icePackage = cls.getPackage().getName();
        String name = cls.getSimpleName().substring(1, cls.getSimpleName().indexOf("Operations"));
        Class<? extends Ice.ObjectPrx> cls2;
        try {
            cls2 = (Class<? extends ObjectPrx>) Class.forName(icePackage + "." + name + "Prx");
        } catch (ClassNotFoundException e) {
            throw new BusinessException(null, BusinessException.Enum.BASECODE_EXCEPTION);
        }
        return cls2;
    }

    /**
     * 根据Api  Class获取服务连接代理
     *
     * @param communicator
     * @param serviceCls
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    @SuppressWarnings({"unchecked"})
    private static <T extends Ice.ObjectPrx> T createIceProxy(Ice.Communicator communicator,
                                                              Class<T> serviceCls, boolean test) {
        T proxy = null;
        String clsName = serviceCls.getName();
        String serviceName = serviceCls.getSimpleName();
        int pos = serviceName.lastIndexOf("Prx");
        if (pos <= 0) {
            throw new java.lang.IllegalArgumentException(
                    "Invalid ObjectPrx class ,class name must end with Prx");
        }
        String realSvName = serviceName.substring(0, pos);
        FileReader reader;
        try {
            if (test) {
                //测试环境 获取代理端口的方式
                Integer point = null;
                try {
                    reader = new FileReader(Sl4jIceBoxTestServer.path);
                    BufferedReader buffer = new BufferedReader(reader);
                    Map<String, Integer> map = JsonUtil.jsonToMap(buffer.readLine(), HashMap.class, String.class, Integer.class);
                    buffer.close();
                    point = map.get(realSvName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                realSvName = realSvName + ":default -p " + point;
            }
            Ice.ObjectPrx base = communicator.stringToProxy(realSvName);
            proxy = (T) Class.forName(clsName + "Helper").newInstance();
            Method m1 = proxy.getClass().getDeclaredMethod("uncheckedCast",
                    ObjectPrx.class);
            proxy = (T) m1.invoke(proxy, base);
            return proxy;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取守护线程  用于测试
     *
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月19日
     * @records <p>  fushun 2017年1月19日</p>
     */
    public static Thread getMonitorThread() {
        return nonitorThread;
    }

    //守护线程
    private static class MonitorThread extends Thread {
        public void run() {
            synchronized (nonitorThreadLock) {
                try {
                    nonitorThreadLock.wait();
                } catch (InterruptedException e1) {
                }
                getICECommunictor();
            }
        }
    }

    /**
     * 代理
     *
     * @version 1.0
     * @date: 2017-11-13 16:54:09
     * @author:wangfushun
     */
    private class IceCglibProxy implements MethodInterceptor {

        private Enhancer enhancer = new Enhancer();

        private Class<? extends Ice.ObjectPrx> otherCls;

        private Object obj;

        /**
         * 获取代理
         *
         * @param clazz
         * @return
         * @date: 2017-11-08 18:22:52
         * @author:wangfushun
         * @version 1.0
         */
        @SuppressWarnings("unchecked")
        public <T> T getProxy(Class<T> clazz, Class<? extends Ice.ObjectPrx> otherCls, boolean test) {
            //设置需要创建子类的类
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(this);
            this.otherCls = otherCls;
            if (test) {
                this.obj = ICEClientUtil.getTestSerivcePrx(this.otherCls);
            } else {
                this.obj = ICEClientUtil.getSerivcePrx(this.otherCls);
            }
            //通过字节码技术动态创建子类实例
            return (T) enhancer.create();
        }

        //实现MethodInterceptor接口方法
        public Object intercept(Object obj, Method method, Object[] args,
                                MethodProxy proxy) throws Throwable {
            //通过代理类调用父类中的方法
//				Object result = proxy.invokeSuper(obj, args);  
            Method method2 = otherCls.getMethod(method.getName(), args[0].getClass());

            Object result = method2.invoke(this.obj, args[0]);

            return result;
        }
    }

}

