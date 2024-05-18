package top.histevehu.srpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.enumeration.LoadBalanceType;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.enumeration.SerializerType;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.factory.SingletonFactory;
import top.histevehu.srpc.core.codec.CommonDecoder;
import top.histevehu.srpc.core.codec.CommonEncoder;
import top.histevehu.srpc.core.loadbalance.LoadBalance;
import top.histevehu.srpc.core.registry.ServiceDiscovery;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * sRPC 基于Netty的客户端
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("Nacos");
    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(SerializerType.DEFAULT, LoadBalanceType.DEFAULT);
    }

    public NettyClient(LoadBalanceType loadBalanceType) {
        this(SerializerType.DEFAULT, loadBalanceType);
    }

    public NettyClient(SerializerType serializerType) {
        this(serializerType, LoadBalanceType.DEFAULT);
    }

    public NettyClient(SerializerType serializerType, LoadBalanceType loadBalanceType) {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.serviceDiscovery.setLoadbalance(LoadBalance.getByCode(loadBalanceType.getCode()));
        this.serializer = CommonSerializer.getByCode(serializerType.getCode());
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时期限, 如果超过此时间或无法建立连接，则连接将失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // ========== outBoundHandler部分 ==========
                        // outboundHandler必须放在最后一个inboundHandler之前,否则无法传到outboundHandler
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new CommonEncoder(serializer))
                                // ========== inBoundHandler部分 ==========
                                // 客户端心跳检查，若5秒内都没有往该链上写入数据，就会调用userEventTriggered方法
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                // ByteBuf -> RpcRequest
                                .addLast(new CommonDecoder())
                                .addLast(new NettyClientHandler(bootstrap));
                    }
                });
    }

    /**
     * 发送请求
     *
     * @param rpcRequest 请求体对象
     * @return 响应结果
     */
    @Override
    public CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
            Channel channel = ChannelProvider.get(inetSocketAddress, bootstrap);
            if (channel != null && !channel.isActive()) {
                eventLoopGroup.shutdownGracefully();
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

    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }
}
