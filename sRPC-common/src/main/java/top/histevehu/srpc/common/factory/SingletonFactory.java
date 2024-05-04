package top.histevehu.srpc.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 */
public class SingletonFactory {
    private static final Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        if (instance == null) {
            synchronized (clazz) {
                if (instance == null) {
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                        objectMap.put(clazz, instance);
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
