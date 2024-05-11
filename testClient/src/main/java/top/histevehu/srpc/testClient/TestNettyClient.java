package top.histevehu.srpc.testClient;

import top.histevehu.srpc.api.manyServices.ServiceA;
import top.histevehu.srpc.api.manyServices.ServiceB;
import top.histevehu.srpc.api.manyServices.ServiceC;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.core.transport.RpcClient;
import top.histevehu.srpc.core.transport.RpcClientProxy;
import top.histevehu.srpc.core.transport.netty.client.NettyClient;

/*
 * 测试用sRPC Netty服务端
 */
public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcServiceProperties serviceProperties = new RpcServiceProperties();
        RpcClientProxy clientProxy = new RpcClientProxy(client, serviceProperties);

        ServiceA serviceA = clientProxy.getProxy(ServiceA.class);
        ServiceB serviceB = clientProxy.getProxy(ServiceB.class);
        ServiceC serviceC = clientProxy.getProxy(ServiceC.class);

        // 测试默认组
        System.out.println("========== 测试默认服务组 ==========");
        System.out.println(serviceA.hello());
        // 测试G1
        System.out.println("========== 测试服务组G1 ==========");
        serviceProperties.setGroup("G1");
        serviceProperties.setVersion("1.0");
        System.out.println(serviceA.hello());
        System.out.println(serviceC.hello());
        serviceProperties.setVersion("2.0");
        System.out.println(serviceA.hello());
        System.out.println(serviceC.hello());
        // 测试G2
        System.out.println("========== 测试服务组G2 ==========");
        serviceProperties.setGroup("G2");
        serviceProperties.setVersion("1.0");
        System.out.println(serviceA.hello());
        System.out.println(serviceB.bye());
        // System.out.println(serviceC.hello());
        serviceProperties.setVersion("2.0");
        System.out.println(serviceA.hello());
        System.out.println(serviceB.bye());
        // 测试G3
        System.out.println("========== 测试服务组G3 ==========");
        serviceProperties.setGroup("G3");
        serviceProperties.setVersion("1.0");
        System.out.println(serviceB.bye());
        System.out.println(serviceC.hello());
    }
}
