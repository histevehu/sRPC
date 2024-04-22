package top.histevehu.srpc.core.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.core.RpcServer;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * sRPC 基于Socket的服务端
 */
public class SocketServer implements RpcServer {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    /**
     * Socket服务端构造方法
     *
     * @param serviceRegistry 已经注册好服务的ServiceRegistry
     */
    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port) {
        logger.info("sRPC服务端正在启动...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("sRPC服务端启动成功，端口：{}", port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接，{}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new WorkerThread(socket, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

}
