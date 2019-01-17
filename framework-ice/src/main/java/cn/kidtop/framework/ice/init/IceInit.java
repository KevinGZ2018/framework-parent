package cn.kidtop.framework.ice.init;

import cn.kidtop.framework.ice.exception.UnknownRPCServiceExcepton;
import cn.kidtop.framework.ice.proxy.IceProxy;
import cn.kidtop.framework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

public class IceInit {
    private static final Logger log = LoggerFactory.getLogger(IceInit.class);
    private Ice.Communicator ic;
    private Ice.Object object;

    public IceInit(ApplicationContext appContext) {
        String serviceName = ProcessInfo.getAppIceServiceName();
        if (StringUtils.isNotEmpty(serviceName)) {
            object = (Ice.Object) appContext.getBean(serviceName);
        } else {
            Class<Ice.Object> serviceType = ProcessInfo.getAppIceServiceType();
            if (serviceType != null) {
                object = appContext.getBeansOfType(serviceType).values().iterator().next();
            } else {
                throw new UnknownRPCServiceExcepton(UnknownRPCServiceExcepton.Enum.UNKNOWN_EXCEPTION);
            }
        }
    }

    public IceInit(ApplicationContext appContext, Class<Ice.Object> serviceType) {
        object = appContext.getBeansOfType(serviceType).values().iterator().next();
    }

    public IceInit(ApplicationContext appContext, String serviceName) {
        object = (Ice.Object) appContext.getBean(serviceName);
    }

    public void start(String[] args) {
        try {
            ic = Ice.Util.initialize(args);
            Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints(
                    ProcessInfo.getAppIceAdapterName(),
                    ProcessInfo.getAppIcePort());
            adapter.add(object, Ice.Util.stringToIdentity(ProcessInfo.getAppIceIdentity()));
            adapter.activate();
            log.info("APP " + ProcessInfo.getAppIceName() + " STARTED ON:" + Instant.now());
            ic.waitForShutdown();
        } catch (Exception e) {
            log.error("RPC Service error: " + e);
            throw new UnknownRPCServiceExcepton(UnknownRPCServiceExcepton.Enum.UNKNOWN_EXCEPTION);
        }
    }

    public void destroy() {
        if (ic != null) {
            ic.destroy();
        }
        IceProxy.destroy();
        log.info("APP " + ProcessInfo.getAppIceName() + " TERMINATED ON:" + Instant.now());
        System.exit(0);
    }
}
