package top.histevehu.srpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.factory.SingletonFactory;
import top.histevehu.srpc.core.loadbalancer.LoadBalancer;
import top.histevehu.srpc.core.loadbalancer.RoundRobinLoadBalancer;
import top.histevehu.srpc.core.registry.NacosServiceDiscovery;
import top.histevehu.srpc.core.registry.ServiceDiscovery;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * sRPC 基于Netty的客户端
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RoundRobinLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializer) {
        this(serializer, new RoundRobinLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    /**
     * 发送请求
     *
     * @param rpcRequest 请求体对象
     * @return 响应结果(协变式返回)
     */
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (channel != null && !channel.isActive()) {
                group.shutdownGracefully();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest));
                    } else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
