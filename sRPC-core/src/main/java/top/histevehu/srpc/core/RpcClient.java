package top.histevehu.srpc.core;

import top.histevehu.srpc.common.entity.RpcRequest;

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
}
