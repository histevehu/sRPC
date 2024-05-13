package top.histevehu.srpc.testServer;


import top.histevehu.srpc.core.annotation.SrpcServiceScan;
import top.histevehu.srpc.core.transport.socket.server.SocketServer;

/**
 * 测试用sRPC Socket服务端
 */
@SrpcServiceScan
public class TestSocketServer {

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.start();
    }

}
