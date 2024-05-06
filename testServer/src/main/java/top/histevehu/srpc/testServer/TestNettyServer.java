package top.histevehu.srpc.testServer;

import top.histevehu.srpc.core.annotation.SrpcServiceScan;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.netty.server.NettyServer;

/*
 * 测试用sRPC Netty服务端
 */
@SrpcServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9001, CommonSerializer.KRYO_SERIALIZER);
        server.start();
    }
}
