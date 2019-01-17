package cn.kidtop.framework.ice.aop;

import Ice.DispatchInterceptor;
import Ice.DispatchStatus;
import Ice.Identity;
import Ice.Request;
import cn.kidtop.framework.constant.SystemConstant;
import cn.kidtop.framework.ice.thread.ThreadIdUtil;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PerfDispatchInterceptor extends DispatchInterceptor {
    public static final Map<String, String> map = new java.util.concurrent.ConcurrentHashMap<String, String>();
    public static final String SERVICE_ADDRES = "serverAddress";
    public static final String REMOTE_ADDRES = "remoteAddress";
    public static final String SERVICE_NAME = "serviceName";
    public static final String METHOD_NAME = "methodName";
    private static final Map<Ice.Identity, Ice.Object> id2ObjectMAP = new java.util.concurrent.ConcurrentHashMap<Ice.Identity, Ice.Object>();
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String ICE_ISA = "ice_isA";
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PerfDispatchInterceptor.class);
    private static PerfDispatchInterceptor self = new PerfDispatchInterceptor();

    public static PerfDispatchInterceptor getINSTANCE() {
        return self;
    }

    public static DispatchInterceptor addICEObject(Ice.Identity id, Ice.Object iceObj) {
        id2ObjectMAP.put(id, iceObj);
        return self;
    }

    public static void removeICEObject(Identity id) {
        logger.info("remove ice object " + id);
        id2ObjectMAP.remove(id);

    }

    /**
     * 此方法可以做任何拦截，类似AOP.
     */
    @Override
    public DispatchStatus dispatch(Request request) {
        Identity theId = request.getCurrent().id;
        ThreadIdUtil.generateThreadUUId();
        // request.getCurrent().con会打印出来 local address = 16.156.210.172:50907
        // （回车换行） remote address = 16.156.210.172:51147 这样的信息
        // 其中 local address 为被访问的服务的地址端口，Remote Address为客户端的地址端
        String[] address = request.getCurrent().con.toString().split("[\\t \\n]+");
        String inf = "dispach req,method:" + request.getCurrent().operation + " service:" + theId.name
                + "  server address:" + address[3].trim() + "  remote address:" + address[7].trim() + " 当前系统版本号:{}" + SystemConstant.VERSION;

        logger.info(inf + " begin");

        try {
            if (!ICE_ISA.equals(request.getCurrent().operation)) {
                // local address = 192.168.1.252:53830
                // remote address = 192.168.1.250:33662

                map.put(SERVICE_ADDRES, address[3].trim());
                map.put(REMOTE_ADDRES, address[7].trim());
                map.put(SERVICE_NAME, theId.name);
                map.put(METHOD_NAME, request.getCurrent().operation);
                ThreadIdUtil.generateThreadInitInfo(map);
            }
            DispatchStatus reslt = id2ObjectMAP.get(request.getCurrent().id).ice_dispatch(request);
            logger.info(inf + " success");
            if (!ICE_ISA.equals(request.getCurrent().operation)) {
//				ThreadIdUtil.removeThreadIceInitInfo();
//				ThreadIdUtil.removeThreadUUId();
            }

            return reslt;
        } catch (RuntimeException e) {
            logger.info(inf + " error " + e);
            throw e;

        }
    }

}
