package top.histevehu.srpc.core.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
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

    /**
     * 添加服务提供对象
     *
     * @param serviceImpl       服务实现实例
     * @param serviceProperties 服务信息
     */
    @Override
    public <T> void addServiceProvider(T serviceImpl, RpcServiceProperties serviceProperties) {
        // serviceName+group+version
        String serviceFullName = serviceProperties.toRpcServiceFullName();
        // 本地服务注册表注册服务时需要先判断该服务组中的接口下是否已经注册了实现类，若已存在则报错
        if (!registeredService.contains(serviceFullName)) {
            registeredService.add(serviceFullName);
            serviceMap.put(serviceFullName, serviceImpl);
            logger.info("注册服务：服务接口 {} 服务实现 {} 服务组 {} 版本 {}",
                    serviceProperties.getServiceName(),
                    serviceImpl.getClass().getCanonicalName(),
                    serviceProperties.getGroup(),
                    serviceProperties.getVersion());
            return;
        }
        logger.error("当前服务组{}内服务接口{}下已经注册有相同版本{}的服务实现", serviceProperties.getGroup(), serviceProperties.getServiceName(), serviceProperties.getVersion());
        throw new RpcException(RpcError.SERVICE_EXISTED);
    }

    /**
     * 获取服务
     *
     * @param serviceFullName 服务名全称（服务组@服务名@版本）
     * @return 服务实体
     * @throws RpcException 若找不到服务，则抛出异常
     */
    @Override
    public Object getServiceProvider(String serviceFullName) {
        Object service = serviceMap.get(serviceFullName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

