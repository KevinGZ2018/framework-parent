package cn.kidtop.framework.ice.thread;

import java.util.Map;
import java.util.UUID;

/**
 * @author zhoup
 */
public final class ThreadIdUtil {

    public static final String UUID_NAME = "handler_id";

    public static final String ICE_INIT_INFO = "ice_init_info";

    public static final String MESSAGE_NO = "message_no";

    public static void generateThreadUUId() {
        if (getThreadUUId() == null) {
            UUID uuid = UUID.randomUUID();
            StaticSession.setAttribute(UUID_NAME, uuid.toString());
        }
    }

    public static void generateThreadInitInfo(Map<String, String> map) {
        if (getThreadUUId() == null) {
            UUID uuid = UUID.randomUUID();
            StaticSession.setAttribute(UUID_NAME, uuid.toString());
        }
        StaticSession.setAttribute(ICE_INIT_INFO, map);
    }

    public static String getThreadUUId() {
        return (String) StaticSession.getAttribute(UUID_NAME);
    }

    @SuppressWarnings("unchecked")
    public static Map<Object, Object> getThreadIceInitInfo() {
        return (Map<Object, Object>) StaticSession.getAttribute(ICE_INIT_INFO);
    }

    public static void setThreadIceInitInfo(Map<Object, Object> map) {
        StaticSession.setAttribute(ICE_INIT_INFO, map);
    }

    public static void removeThreadUUId() {
        StaticSession.removeAttribute(UUID_NAME);
    }

    public static void removeThreadIceInitInfo() {
        StaticSession.removeAttribute(ICE_INIT_INFO);
    }
}
