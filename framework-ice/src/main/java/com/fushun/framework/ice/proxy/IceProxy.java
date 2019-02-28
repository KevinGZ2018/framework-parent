package com.fushun.framework.ice.proxy;

import com.fushun.framework.ice.init.ProcessInfo;
import com.fushun.framework.util.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//import me.jor.util.Help;

public class IceProxy {
    private static Ice.Communicator ic;
    private static Map<String, Object> prx = new ConcurrentHashMap<>();

    private static void initIc(Class<?> cls, String... args) {
        if (ic == null) {
            synchronized (cls) {
                if (ic == null) {
                    ic = Ice.Util.initialize(CollectionUtils.isEmpty(args) ? ProcessInfo.getMainArgs() : args);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Ice.ObjectPrxHelperBase> T initPrx(Class<T> cls, String proxyName) {
        return (T) IceProxy.prx.computeIfAbsent(proxyName, pn -> {
            Ice.ObjectPrx prx = ic.stringToProxy(pn);
            try {
                Method method = cls.getMethod("checkedCast", Ice.ObjectPrx.class);
                return method.invoke(null, prx);
            } catch (Exception e) {
                throw new IllegalArgumentException(cls + ";" + pn, e);
            }
        });
    }

    public static <T extends Ice.ObjectPrxHelperBase> T init(Class<T> cls, String proxyName, String... args) {
        initIc(cls, args);
        return initPrx(cls, proxyName);
    }

    public static void destroy() {
        if (ic != null) {
            ic.destroy();
        }
    }
}
