package top.histevehu.srpc.testClient;


import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddObject;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.serializer.KryoSerializer;
import top.histevehu.srpc.core.transport.RpcClientProxy;
import top.histevehu.srpc.core.transport.socket.client.SocketClient;

/**
 * 测试用客户端
 */
public class TestSocketClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(7, "这是通过sRPC Socket远程调用HelloService的测试");
        String helloServiceRes = helloService.hello(object);
        System.out.println(helloServiceRes);

        TestCountAddService test = proxy.getProxy(TestCountAddService.class);
        TestCountAddObject testCountAddObject = new TestCountAddObject(1, 2);
        Integer TestCountAddServiceRes = test.countAdd(testCountAddObject);
        System.out.println(TestCountAddServiceRes);
    }

}
