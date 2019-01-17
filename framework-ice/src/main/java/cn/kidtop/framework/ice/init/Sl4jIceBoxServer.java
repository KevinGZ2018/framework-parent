package cn.kidtop.framework.ice.init;

import IceBox.Server;
import cn.kidtop.framework.ice.log.Sl4jLogerI;
import cn.kidtop.framework.ice.spring.SpringUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Sl4jIceBoxServer {

    @SuppressWarnings("unused")
    private static ClassPathXmlApplicationContext ctx = SpringUtil.getContext();//初始化Spring

    public static void main(String[] args) {
        //初始化ice
        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();
        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");
        Ice.Logger logger = new Sl4jLogerI("IceInit");
        Ice.Util.setProcessLogger(logger);
        initData.logger = logger;

        Server server = new Server();
        System.exit(server.main("IceBox.Server", args, initData));
    }

}
