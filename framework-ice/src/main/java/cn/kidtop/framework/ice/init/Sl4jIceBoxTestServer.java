package cn.kidtop.framework.ice.init;

import IceBox.Server;
import cn.kidtop.framework.ice.log.Sl4jLogerI;
import cn.kidtop.framework.util.JsonUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Sl4jIceBoxTestServer {

    public static String path = System.getProperty("user.dir") + "\\src\\main\\resources\\IceBoxPort.json";
    private static int startPort = 4061;

    public static void main(String[] args) throws IOException {

        ClassPathScanningCandidateComponentProvider c = new ClassPathScanningCandidateComponentProvider(false);
        TypeFilter includeFilter = new RegexPatternTypeFilter(Pattern.compile(".*\\.publish\\..*Service"));
        c.addIncludeFilter(includeFilter);
        Set<BeanDefinition> set = c.findCandidateComponents("cn.kidtop.publish");

        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();
//		#server properties
        initData.properties.setProperty("IceBox.InstanceName", "WebAppIceBox 1");
        initData.properties.setProperty("IceBox.InheritProperties", "1");
        initData.properties.setProperty("IceBox.PrintServicesReady", "WebAppIceBox 1");
//		#IceBox.ServiceManager.Endpoints=tcp -p 9999 -h 192.168.1.108
//		#performance properties
        initData.properties.setProperty("Ice.ThreadPool.Server.Size", "4");
        initData.properties.setProperty("Ice.ThreadPool.Server.SizeMax", "100");
        initData.properties.setProperty("Ice.ThreadPool.Server.SizeWarn", "40");
        initData.properties.setProperty("Ice.ThreadPool.Client.Size", "4");
        initData.properties.setProperty("Ice.ThreadPool.Client.SizeMax", "100");
        initData.properties.setProperty(" Ice.ThreadPool.Client.SizeWarn", "40");
//		#for system stronger
        initData.properties.setProperty("Ice.Override.ConnectTimeout", "30000");
        initData.properties.setProperty(" Ice.ACM.Client", "300");
        initData.properties.setProperty("Ice.ACM.Server", "300");
//		#logger and trace
//		#Ice.LogFile=iceserv.log
        initData.properties.setProperty("Ice.PrintStackTraces", "1");
        initData.properties.setProperty("Ice.Trace.Retry", "2");
        initData.properties.setProperty("Ice.Trace.Network", "2");
        initData.properties.setProperty("Ice.Trace.ThreadPool", "1");
        initData.properties.setProperty("Ice.Trace.Locator", "2");
        initData.properties.setProperty("Ice.Warn.Connections", "1");
        initData.properties.setProperty("Ice.Warn.Dispatch", "1");
        initData.properties.setProperty("Ice.Warn.Endpoints", "1");
        initData.properties.setProperty("Ice.Default.Package", "cn.kidtop.generated.ice.v1");
        Map<String, Integer> map = new HashMap<>();
        for (BeanDefinition beanDefinition : set) {
            String classPtah = beanDefinition.getBeanClassName();
            String className = classPtah.substring(classPtah.lastIndexOf(".") + 1, classPtah.length());
            System.out.println(className);
            map.put(className, startPort);
            initData.properties.setProperty("IceBox.Service." + className, classPtah + " prop1 1");
            initData.properties.setProperty(className + ".Endpoints", "tcp -p " + startPort + " -h localhost");
            startPort++;
        }

        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(JsonUtil.classToJson(map));
        fileWriter.close();
//		file.
        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");
        Ice.Logger logger = new Sl4jLogerI("IceInit");
        Ice.Util.setProcessLogger(logger);
        initData.logger = logger;

        Server server = new Server();
        System.exit(server.main("IceBox.Server", args, initData));
    }
}
