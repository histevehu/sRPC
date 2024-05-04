package top.histevehu.srpc.core.registry;

import java.net.InetSocketAddress;

/**
 * 注册中心服务注册相关接口
 */
public interface ServiceRegistry {
    /**
     * 向注册中心注册服务端服务
     *
     * @param serviceName       服务名称
     * @param inetSocketAddress 提供服务的地址和端口
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
