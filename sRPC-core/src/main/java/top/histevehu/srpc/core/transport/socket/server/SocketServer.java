package top.histevehu.srpc.core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.factory.ThreadPoolFactory;
import top.histevehu.srpc.core.handler.RequestHandler;
import top.histevehu.srpc.core.hook.ShutdownHook;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.provider.ServiceProviderImpl;
import top.histevehu.srpc.core.registry.NacosServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * sRPC 基于Socket的服务端
 */
public class SocketServer implements RpcServer {

    private final ExecutorService threadPool;

    private final String host;
    private final int port;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public <T> void regService(T service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
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
