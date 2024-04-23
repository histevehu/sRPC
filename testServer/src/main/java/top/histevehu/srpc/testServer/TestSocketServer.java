package top.histevehu.srpc.testServer;


import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.registry.DefaultServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.serializer.KryoSerializer;
import top.histevehu.srpc.core.socket.server.SocketServer;

/**
 * 测试用sRPC Socket服务端
 */
public class TestSocketServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        TestCountAddService testCountAddService = new TestCountAddServiceImpl();
        // 创建注册中心并注册服务
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService)
                .register(testCountAddService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.setSerializer(new KryoSerializer());
        socketServer.start(9000);
    }

}
