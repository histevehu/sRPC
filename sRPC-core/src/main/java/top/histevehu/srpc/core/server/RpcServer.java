package top.histevehu.srpc.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * sRPC服务端（远程方法提供方）
 */
public class RpcServer {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    /**
     * 初始化 sRPC 服务端
     * @param serviceRegistry 已经注册好服务的ServiceRegistry
     */
    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    /**
     * 启动sRPC 服务端
     * @param port 端口
     */
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
