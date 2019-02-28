package com.fushun.framework.ice.init;

import Ice.Communicator;
import Ice.Identity;
import IceBox.Service;
import com.fushun.framework.ice.aop.PerfDispatchInterceptor;
import com.fushun.framework.ice.log.Sl4jLogerI;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public abstract class AbstractIceBoxService implements Service {
    protected static org.slf4j.Logger logger = LoggerFactory
            .getLogger(AbstractIceBoxService.class);
    protected static Sl4jLogerI iceLogger = new Sl4jLogerI("communicator");
    protected Ice.ObjectAdapter _adapter;
    protected Identity id;

    @Override
    public void start(String name, Communicator communicator, String[] args) {
        Ice.Util.setProcessLogger(iceLogger);
        // 创建objectAdapter，这里和service同名
        _adapter = communicator.createObjectAdapter(name);
        // 创建servant并激活
        //根究服务名称从spring中调用服务
        Ice.Object object = this.createMyIceServiceObj(args);
//		String replaceChar = name.substring(0,1);
//		String beanName = replaceChar.toLowerCase()+name.substring(1,name.length())+"Impl";
//		Ice.Object object = (Ice.Object)SpringUtil.getBean(beanName);
        id = communicator.stringToIdentity(name);
        // _adapter.add(object, communicator.stringToIdentity(name));
        _adapter.add(PerfDispatchInterceptor.addICEObject(id, object), id);
        _adapter.activate();
        logger.info(name + "  service started ,with param size " + args.length
                + " detail:" + Arrays.toString(args));
    }

    /**
     * 创建具体的ICE 服务实例对象
     *
     * @param args 服务的配置参数，来自icegrid.xml文件
     * @return Ice.Object
     */
    public abstract Ice.Object createMyIceServiceObj(String[] args);

    @Override
    public void stop() {
        logger.info("stopping service " + id + " ....");
        _adapter.destroy();
        PerfDispatchInterceptor.removeICEObject(id);
        logger.info("stopped service " + id + " stoped");

    }

}
