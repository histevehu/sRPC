package top.histevehu.srpc.testServer;

import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.netty.server.NettyServer;

/*
 * 测试用sRPC Netty服务端
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceNettyImpl();
        TestCountAddService testCountAddService = new TestCountAddServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1", 9001, CommonSerializer.KRYO_SERIALIZER);
        server.regService(helloService, HelloService.class);
        server.regService(testCountAddService, TestCountAddService.class);
        server.start();
    }
}
