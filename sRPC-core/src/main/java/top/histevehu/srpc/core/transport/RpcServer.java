package top.histevehu.srpc.core.transport;

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
     * 注册服务。包括向本地服务注册表和注册中心注册
     *
     * @param service
     * @param serviceClass
     */
    <T> void regService(T service, Class<T> serviceClass);

}
