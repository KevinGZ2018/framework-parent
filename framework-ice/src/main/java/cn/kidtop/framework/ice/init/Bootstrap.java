package cn.kidtop.framework.ice.init;

import cn.kidtop.framework.ice.log.Sl4jLogerI;
import cn.kidtop.framework.ice.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 使用Bootstrap启动就不要再使用IceBoxInit，也不需要每个服务模块创建自己的main方法类
 */
public class Bootstrap {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    ;
    private static ClassPathXmlApplicationContext appContext = SpringUtil.getContext();
    private static IceInit iceInit;

    public static void startup(String[] args) throws BeansException, ClassNotFoundException {
        iceInit = new IceInit(appContext);
        try {
            iceInit.start(args);
        } finally {
            log.error("startup fail, ice service stop");
            iceInit.destroy();
        }
    }

    public static void main(String[] args) throws BeansException, ClassNotFoundException {
        Ice.Logger logger = new Sl4jLogerI("IceInit");
        Ice.Util.setProcessLogger(logger);
        ProcessInfo.setMainArgs(args);
        startup(args);
    }
}
