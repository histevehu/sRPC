package top.histevehu.srpc.core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.factory.ThreadPoolFactory;
import top.histevehu.srpc.core.handler.RequestHandler;
import top.histevehu.srpc.core.hook.ShutdownHook;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * sRPC 基于Socket的服务端
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler;

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serializer = CommonSerializer.getByCode(serializer);
        this.requestHandler = new RequestHandler();
        scanServices();
    }

    public void start() {
        logger.info("sRPC服务端正在启动...");
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("sRPC服务端启动成功，端口：{}", port);
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接，{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketServerHandlerThread(socket, requestHandler, serializer));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        } finally {
            threadPool.shutdown();
        }
    }

}
