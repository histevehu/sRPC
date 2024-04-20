package top.histevehu.srpc.test;


import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;
import top.histevehu.srpc.core.client.RpcClientProxy;

/**
 * 测试用客户端
 */
public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(7, "这是通过sRPC远程调用HelloService的测试");
        String res = helloService.hello(object);
        System.out.println(res);
    }

}
