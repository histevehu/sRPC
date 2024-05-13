package top.histevehu.srpc.common.extension;

import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.util.Holder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 扩展类加载
 */
@Slf4j
public final class ExtensionLoader<T> {

    // 扩展类配置默认存放位置
    private static final String EXTENSION_DIRECTORY = "META-INF/extensions/";
    // 全局存储<接口类型，扩展类加载器>
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    // 全局存储<接口类型，扩展类实例>
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    // 每个扩展类加载器内缓存其类型及实例
    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 获取指定接口类型的扩展类加载器
     *
     * @param type 接口类型
     */
    @SuppressWarnings("unchecked")
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension 类型不能为空");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension 类型应为接口");
        }
        if (type.getAnnotation(SrpcSPI.class) == null) {
            throw new IllegalArgumentException("Extension 必须被@SrpcSPI注解");
        }
        // 获取扩展类加载器，若不存在则创建
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension 名称不能为空");
        }
        // 首先从缓存中获取，如果没有命中，则创建一个
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // 如果不存在实例，则创建单一实例
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * 从文件加载所有 T 类型的扩展类，并按名称获取特定的扩展类
     *
     * @param name
     * @return
     */
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("Fail to create an instance of the extension class " + clazz);
            }
        }
        return instance;
    }

    /**
     * 从缓存中获取加载的扩展类
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        // 若扩展类不存在则双重校验创建
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // 从 extensions 目录加载所有扩展
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.EXTENSION_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // 读每一行
            while ((line = reader.readLine()) != null) {
                // 获取注释索引
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // # 后面的字符串是注释，所以忽略
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // sRPC SPI 使用键值对，因此两者都不能为空
                        if (!name.isEmpty() && !clazzName.isEmpty()) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
