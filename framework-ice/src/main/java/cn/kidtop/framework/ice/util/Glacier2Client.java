package cn.kidtop.framework.ice.util;

import Glacier2.SessionHelper;
import Glacier2.SessionNotExistException;
import Ice.ObjectPrx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Glacier2客户端测试 <br>
 * 客户端使用SSL通过Glacier2，调用服务。<br>
 * 而服务是使用Grid
 *
 * @author zhoup
 */
public class Glacier2Client {

    public static Glacier2.SessionHelper _sessionHelper;
    private static Glacier2.SessionFactoryHelper sessionFactoryHelper;

    @SuppressWarnings("rawtypes")
    public static void connect(final Class serviceCls, final Glacier2Callback callback) {
        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();

        try {
            File file = new File(
                    "D:/chaoaisong/superisong-app-appice/src/main/resources/iceclientGlacier2.properties");
            FileInputStream inputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(inputStream);
            for (String name : properties.stringPropertyNames()) {
                if (name.equals("idleTimeOutSeconds")) {
                    continue;
                }
                String value = properties.getProperty(name);
                initData.properties.setProperty(name, value);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        sessionFactoryHelper = new Glacier2.SessionFactoryHelper(initData, new Glacier2.SessionCallback() {

            @Override
            public void connectFailed(SessionHelper arg0, Throwable ex) {
                System.out.println("sessionHelper connectFailed");
                ex.printStackTrace();
            }

            @Override
            public void connected(SessionHelper sessionHelper) throws SessionNotExistException {
                System.out.println("sessionHelper connected");
                String clsName = serviceCls.getName();
                System.out.println("clsName:" + clsName);
                String serviceName = serviceCls.getSimpleName();
                int pos = serviceName.lastIndexOf("Prx");
                if (pos <= 0) {
                    throw new java.lang.IllegalArgumentException(
                            "Invalid ObjectPrx class ,class name must end with Prx");
                }
                String realSvName = serviceName.substring(0, pos);
                System.out.println("realSvName:" + realSvName);
                try {
                    Ice.ObjectPrx base = _sessionHelper.communicator().stringToProxy(realSvName);

                    ObjectPrx proxy = (ObjectPrx) Class.forName(clsName + "Helper").newInstance();
                    Method m1 = proxy.getClass().getDeclaredMethod("uncheckedCast", ObjectPrx.class);
                    proxy = (ObjectPrx) m1.invoke(proxy, base);

                    callback.callback(proxy);

                    System.out.println("=========================");

                    _sessionHelper.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void disconnected(SessionHelper sessionHelper) {
                System.out.println("sessionHelper disconnected");
            }

            @Override
            public void createdCommunicator(SessionHelper arg0) {
                // TODO Auto-generated method stub

            }
        });
        _sessionHelper = sessionFactoryHelper.connect("test", "123456");
    }

    // 测试
    public static void main(String[] s) throws InterruptedException {
//		Glacier2Callback callback = new Glacier2Callback() {
//			@Override
//			public void callback(ObjectPrx proxy) {
//				AppShopServicePrx appShopServicePrx = (AppShopServicePrx) proxy;
////				Order[] orders = appShopServicePrx.queryMyOrders("13631276694");
//				if (orders != null) {
//					System.out.println("orders.length:" + orders.length);
//				} else {
//					System.out.println("orders is null");
//				}
//			}
//		};
//
//		Glacier2Client.connect(TicketServicePrx.class, callback);
//		Thread.sleep(2000);
    }
}
