package top.histevehu.srpc.testClient;

import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddObject;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcClient;
import top.histevehu.srpc.core.transport.RpcClientProxy;
import top.histevehu.srpc.core.transport.socket.client.SocketClient;

/**
 * 测试用客户端
 */
public class TestSocketClient {

    public static void main(String[] args) {
        RpcClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        for (int i = 0; i < 20; i++) {
            HelloObject object = new HelloObject(i, "Hello World!");
            String res = helloService.hello(object);
            System.out.println(res);
        }

        TestCountAddService testCountAddService = rpcClientProxy.getProxy(TestCountAddService.class);
        for (int i = 0; i < 20; i++) {
            TestCountAddObject testCountAddObject = new TestCountAddObject(i, 1);
            String res = testCountAddService.countAdd(testCountAddObject);
            System.out.println(res);
        }
    }

}
