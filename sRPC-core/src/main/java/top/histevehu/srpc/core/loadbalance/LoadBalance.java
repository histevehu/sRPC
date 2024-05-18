package top.histevehu.srpc.core.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.extension.SrpcSPI;

import java.util.List;

/**
 * sRPC负载均衡接口。被客户端调用并根据相应的负载均衡算法向服务提供者发送请求
 */
@SrpcSPI
public interface LoadBalance {

    String[] LBs = {"RANDOM", "ROUND_ROBIN", "CONSISTENT_HASH"};

    /**
     * 从给定的实例中根据负载均衡算法选出一台返回
     *
     * @param instances  实例列表
     * @param rpcRequest 服务全名
     */
    default Instance select(List<Instance> instances, RpcRequest rpcRequest) {
        return null;
    }

    /**
     * 根据编码获取负载均衡算法
     */
    static LoadBalance getByCode(int code) {
        if (code < LBs.length && code >= 0) {
            return ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(LBs[code]);
        }
        throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
    }
}
