package top.histevehu.srpc.core.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/*
 * 本地服务注册表，保存服务端本地的服务
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

    // 保存服务名与提供服务的对象的对应关系
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    // 保存当前已经被注册的服务对象名
    private static final Set<String> registeredService = new CopyOnWriteArraySet<>();

    @Override
    public <T> ServiceProvider addServiceProvider(T service, Class<T> serviceClass) {
        // 获取服务类从java语言规范定义的格式输出
        String serviceName = serviceClass.getCanonicalName();
        if (!registeredService.contains(serviceName)) {
            registeredService.add(serviceName);
            serviceMap.put(serviceName, service);
            logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
        }
        return this;
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务实体
     * @throws RpcException 若找不到服务，则抛出异常
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

