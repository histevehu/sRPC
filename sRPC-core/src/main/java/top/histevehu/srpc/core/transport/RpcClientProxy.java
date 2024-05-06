package top.histevehu.srpc.core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.util.RpcMessageChecker;
import top.histevehu.srpc.core.transport.netty.client.NettyClient;
import top.histevehu.srpc.core.transport.socket.client.SocketClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * sRPC客户端动态代理
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient rpcClient) {
        this.client = rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .heartBeat(false)
                .build();
        RpcResponse rpcResponse = switch (client) {
            case NettyClient nc -> {
                CompletableFuture<RpcResponse> completableFuture = nc.sendRequest(rpcRequest);
                try {
                    yield completableFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("调用方法: {}#{} 发生错误：{}", method.getDeclaringClass().getName(), method.getName(), e.getMessage());
                    yield null;
                }
            }
            case SocketClient sc -> sc.sendRequest(rpcRequest);
            default -> {
                logger.error("Unsupported RPC Client: {}", client);
                yield null;
            }
        };
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse != null ? rpcResponse.getData() : null;
    }
}
