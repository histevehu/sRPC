package top.histevehu.srpc.core;

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
    void start(int port);

    /**
     * 设置序列化反序列化器
     *
     * @param serializer 序列化器
     */
    void setSerializer(CommonSerializer serializer);
}
