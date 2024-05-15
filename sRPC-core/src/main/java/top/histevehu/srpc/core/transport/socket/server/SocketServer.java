package top.histevehu.srpc.core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.factory.ThreadPoolFactory;
import top.histevehu.srpc.core.handler.RequestHandler;
import top.histevehu.srpc.core.hook.ShutdownHook;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * sRPC 基于Socket的服务端
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private final RequestHandler requestHandler;
    private CommonSerializer serializer;

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer() {
        this(DEFAULT_SERIALIZER);
    }

    public SocketServer(Integer serializer) {
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serializer = CommonSerializer.getByCode(serializer);
        this.requestHandler = new RequestHandler();
        scanServices();
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public void start() {
        logger.info("sRPC服务端正在启动...");
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT));
            logger.info("sRPC服务端启动成功，端口：{}", PORT);
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接，{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketServerHandlerRunnable(socket, requestHandler, serializer));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        } finally {
            threadPool.shutdown();
        }
    }

}
