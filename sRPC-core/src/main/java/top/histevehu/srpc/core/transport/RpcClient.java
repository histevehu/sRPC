package top.histevehu.srpc.core.transport;

import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.extension.SrpcSPI;
import top.histevehu.srpc.core.serializer.CommonSerializer;

/**
 * sRPC客户端接口（调用远程方法方）
 */
@SrpcSPI
public interface RpcClient {

    /**
     * 默认（反）序列化器
     */
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    /**
     * 发送RPC请求
     *
     * @param rpcRequest 请求体对象
     * @return 响应对象
     */
    Object sendRequest(RpcRequest rpcRequest);

    default void shutdown() {
    }
}
