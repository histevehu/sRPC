package top.histevehu.srpc.core.registry;

/**
 * 服务端服务注册相关接口
 */
public interface ServiceRegistry {
    /**
     * 注册服务端服务
     *
     * @param service 代注册的服务
     * @param <T>     服务实体类型
     */
    <T> ServiceRegistry register(T service);

    /**
     * 根据服务名称获取服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    Object getService(String serviceName);
}
