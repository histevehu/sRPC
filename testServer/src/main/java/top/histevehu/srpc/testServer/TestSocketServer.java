package top.histevehu.srpc.testServer;


import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.socket.server.SocketServer;

/**
 * 测试用sRPC Socket服务端
 */
public class TestSocketServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        TestCountAddService testCountAddService = new TestCountAddServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9000, CommonSerializer.HESSIAN_SERIALIZER);
        socketServer.regService(helloService, HelloService.class);
        socketServer.regService(testCountAddService, TestCountAddService.class);
        socketServer.start();
    }

}
