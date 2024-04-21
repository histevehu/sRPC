package top.histevehu.srpc.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.enumeration.ResponseCode;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 服务端处理远程方法的工作线程
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
            Object returnObject = invokeMethod(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }

    /**
     * 通过反射调用请求的方法
     * @param rpcRequest RPC请求
     * @return 方法调用结果
     */
    private Object invokeMethod(RpcRequest rpcRequest) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        // 通过注册中心获得服务
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }

}
