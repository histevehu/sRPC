package top.histevehu.srpc.core.registry;

import top.histevehu.srpc.common.extension.SrpcSPI;
import top.histevehu.srpc.core.loadbalancer.LoadBalancer;

import java.net.InetSocketAddress;

/**
 * 注册中心服务发现相关接口
 */
@SrpcSPI
public interface ServiceDiscovery {
    /**
     * 根据服务名称从服务中心查找获取服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);

    default void setLoadbalance(LoadBalancer lb) {
        throw new UnsupportedOperationException();
    }
}
