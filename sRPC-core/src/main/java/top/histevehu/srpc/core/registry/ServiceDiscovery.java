package top.histevehu.srpc.core.registry;

import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.extension.SrpcSPI;
import top.histevehu.srpc.core.loadbalance.LoadBalance;

import java.net.InetSocketAddress;

/**
 * 注册中心服务发现相关接口
 */
@SrpcSPI
public interface ServiceDiscovery {
    /**
     * 根据服务名称从服务中心查找获取服务实体
     *
     * @param rpcRequest 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);

    default void setLoadbalance(LoadBalance lb) {
        throw new UnsupportedOperationException();
    }
}
