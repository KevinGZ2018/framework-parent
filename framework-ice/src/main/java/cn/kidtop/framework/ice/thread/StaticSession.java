/**
 *
 */
package cn.kidtop.framework.ice.thread;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoup
 */
@SuppressWarnings("serial")
public final class StaticSession implements Serializable {

    private static final InheritableThreadLocal<Map<String, Object>> SESSION =
            new InheritableThreadLocal<Map<String, Object>>();

    private StaticSession() {
    }

    private static void init() {
        if (SESSION.get() == null) {
            Map<String, Object> sessionMap = new HashMap<String, Object>();
            SESSION.set(sessionMap);
        }
    }

    public static void setAttribute(String name, Object value) {
        init();
        SESSION.get().put(name, value);
    }

    public static Object getAttribute(String name) {
        init();
        return SESSION.get().get(name);
    }

    public static <T> void setAttribute(Class<T> clazz, T value) {
        init();
        SESSION.get().put(clazz.getName(), value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(Class<T> clazz) {
        init();
        return (T) SESSION.get().get(clazz.getName());
    }

    public static <T> void removeAttribute(String key) {
        if (SESSION.get() != null) {
            SESSION.get().remove(key);
        }
    }
}
