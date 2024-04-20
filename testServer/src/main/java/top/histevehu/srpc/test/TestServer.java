package top.histevehu.srpc.test;


import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.core.server.RpcServer;

/**
 * 测试用服务端
 */
public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(helloService, 9000);
    }

}
