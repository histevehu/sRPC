package top.histevehu.srpc.core.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册中心相关接口
 */
public interface ServiceRegistry {
    /**
     * 向注册中心注册服务端服务
     *
     * @param serviceName       服务名称
     * @param inetSocketAddress 提供服务的地址和端口
     */
    default void register(String serviceName, InetSocketAddress inetSocketAddress) {
    }

    /**
     * 根据服务名称从服务中心获取服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    default InetSocketAddress getService(String serviceName) {
        return null;
    }
}
