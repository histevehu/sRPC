package top.histevehu.srpc.core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.util.RpcMessageChecker;
import top.histevehu.srpc.core.transport.netty.client.NettyClient;
import top.histevehu.srpc.core.transport.socket.client.SocketClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * sRPC客户端动态代理类。代理对应一个服务的组别、版本等
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;
    // 定义了该代理的服务信息（服务组、版本等）
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcClient rpcClient) {
        this.client = rpcClient;
        this.rpcServiceProperties = new RpcServiceProperties();
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceProperties rpcServiceProperties) {
        this.client = rpcClient;
        this.rpcServiceProperties = rpcServiceProperties;
    }

    /**
     * 通过 RpcClientProxy.newProxyInstance()方法获取某个服务接口类的代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 当动态代理对象调用一个方法的时候，实际调用的是下面的 invoke 方法。
     * 正是因为动态代理才让客户端调用的远程方法像是调用本地方法一样（屏蔽了中间过程）
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(rpcServiceProperties.getServiceName().isEmpty() ?
                        method.getDeclaringClass().getName() : rpcServiceProperties.getServiceName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .heartBeat(false)
                // 将代理的服务信息写入请求
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        // 消费者的客户端类型决定了请求方式
        // 若客户端使用原生Socket则该步调用使用 BIO，如选用 Netty 方式则该步调用使用 NIO。
        RpcResponse<Object> rpcResponse = switch (client) {
            case NettyClient nc -> {
                try {
                    CompletableFuture<RpcResponse<Object>> completableFuture = nc.sendRequest(rpcRequest);
                    yield completableFuture.get();
                } catch (Exception e) {
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
