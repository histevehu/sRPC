package top.histevehu.srpc.testServer;

import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.core.netty.server.NettyServer;
import top.histevehu.srpc.core.registry.DefaultServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;

/*
 * 测试用sRPC Netty服务端
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9001);
    }
}
