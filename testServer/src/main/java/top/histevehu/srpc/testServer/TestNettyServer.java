package top.histevehu.srpc.testServer;

import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.netty.server.NettyServer;
import top.histevehu.srpc.core.registry.DefaultServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.serializer.KryoSerializer;

/*
 * 测试用sRPC Netty服务端
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        TestCountAddService testCountAddService = new TestCountAddServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService)
                .register(testCountAddService);
        NettyServer server = new NettyServer();
        server.setSerializer(new KryoSerializer());
        server.start(9001);
    }
}
