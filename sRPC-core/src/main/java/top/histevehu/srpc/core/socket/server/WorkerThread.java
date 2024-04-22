package top.histevehu.srpc.core.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.core.RpcServerRequestHandler;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * sRPC 基于Socket的服务端的处理远程方法的工作线程
 */
public class WorkerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private final Socket socket;
    private final ServiceRegistry serviceRegistry;

    public WorkerThread(Socket socket, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 通过注册中心获得服务
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object returnObject = RpcServerRequestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
