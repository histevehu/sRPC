package top.histevehu.srpc.core.transport;

import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.core.serializer.CommonSerializer;

/**
 * sRPC客户端接口（调用远程方法方）
 */
public interface RpcClient {
    /**
     * 发送RPC请求
     *
     * @param rpcRequest 请求体对象
     * @return 响应对象
     */
    Object sendRequest(RpcRequest rpcRequest);

    /**
     * 设置序列化反序列化器
     *
     * @param serializer 序列化器
     */
    void setSerializer(CommonSerializer serializer);
}
