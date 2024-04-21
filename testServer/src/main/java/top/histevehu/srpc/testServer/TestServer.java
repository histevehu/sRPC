package top.histevehu.srpc.testServer;


import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.registry.DefaultServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.server.RpcServer;

/**
 * 测试用服务端
 */
public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        TestCountAddService testCountAddService = new TestCountAddServiceImpl();
        // 创建注册中心并注册服务
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService)
                .register(testCountAddService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }

}
