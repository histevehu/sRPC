package top.histevehu.srpc.testServer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.histevehu.srpc.core.annotation.SrpcServiceScanSpring;
import top.histevehu.srpc.core.transport.netty.server.NettyServer;

/*
 * 测试用sRPC Netty服务端
 */
@SrpcServiceScanSpring
public class TestNettySpringServer {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(TestNettySpringServer.class);
        NettyServer server = new NettyServer();
        server.start();
    }
}
