package top.histevehu.srpc.core.transport;

import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.core.serializer.CommonSerializer;

/**
 * sRPC服务端接口（远程方法提供方）
 */
public interface RpcServer {

    /**
     * 默认（反）序列化器
     */
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    /**
     * 启动sRPC 服务端
     *
     * @param port 端口
     */
    void start();

    /**
     * 注册服务
     *
     * @param serviceClass
     * @param service
     * @param serviceProperties
     */
    <T> void regService(T service, RpcServiceProperties serviceProperties);

}
