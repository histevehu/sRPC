package top.histevehu.srpc.testClient;


import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.api.TestCountAddObject;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.client.RpcClientProxy;

/**
 * 测试用客户端
 */
public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(7, "这是通过sRPC远程调用HelloService的测试");
        String helloServiceRes = helloService.hello(object);
        System.out.println(helloServiceRes);

        TestCountAddService test = proxy.getProxy(TestCountAddService.class);
        TestCountAddObject testCountAddObject = new TestCountAddObject(1, 2);
        Integer TestCountAddServiceRes = test.countAdd(testCountAddObject);
        System.out.println(TestCountAddServiceRes);
    }

}
