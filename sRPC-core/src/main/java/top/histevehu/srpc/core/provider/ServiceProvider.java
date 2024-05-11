package top.histevehu.srpc.core.provider;

import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.exception.RpcException;

/**
 * sRPC服务端保存和提供服务实例对象的接口类
 */
public interface ServiceProvider {
    /**
     * 添加提供服务的实例对象
     */
    <T> void addServiceProvider(T service, RpcServiceProperties serviceProperties) throws RpcException;

    /**
     * 获取提供服务的实例对象
     *
     * @param serviceName 服务名
     * @return 服务实例对象
     */
    Object getServiceProvider(String serviceName);

}