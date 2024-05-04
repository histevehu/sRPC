package top.histevehu.srpc.core.transport;

import top.histevehu.srpc.core.serializer.CommonSerializer;

/**
 * sRPC服务端接口（远程方法提供方）
 */
public interface RpcServer {
    /**
     * 启动sRPC 服务端
     *
     * @param port 端口
     */
    void start();

    /**
     * 设置序列化反序列化器
     *
     * @param serializer 序列化器
     */
    void setSerializer(CommonSerializer serializer);

    /**
     * 注册服务。包括向本地服务注册表和注册中心注册
     *
     * @param service
     * @param serviceClass
     */
    <T> void regService(Object service, Class<T> serviceClass);

}
