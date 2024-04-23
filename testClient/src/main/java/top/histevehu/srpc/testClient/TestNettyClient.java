package top.histevehu.srpc.testClient;

import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddObject;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.RpcClient;
import top.histevehu.srpc.core.RpcClientProxy;
import top.histevehu.srpc.core.netty.client.NettyClient;
import top.histevehu.srpc.core.serializer.KryoSerializer;

/*
 * 测试用sRPC Netty服务端
 */
public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9001);
        client.setSerializer(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(8, "这是通过sRPC Netty远程调用HelloService的测试");
        String res = helloService.hello(object);
        System.out.println(res);

        TestCountAddService testCountAddService = rpcClientProxy.getProxy(TestCountAddService.class);
        TestCountAddObject testCountAddObject = new TestCountAddObject(8, 1);
        Integer testCountAddRes = testCountAddService.countAdd(testCountAddObject);
        System.out.println(testCountAddRes);
    }
}
