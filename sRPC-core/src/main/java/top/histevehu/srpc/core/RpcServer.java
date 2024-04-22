package top.histevehu.srpc.core;

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
}
