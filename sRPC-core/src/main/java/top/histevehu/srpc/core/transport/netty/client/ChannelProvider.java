package top.histevehu.srpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * 用于获取 Netty Channel 对象
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, Bootstrap bootstrap) throws InterruptedException {
        String key = inetSocketAddress.toString();
        Channel newChannel;
        synchronized (ChannelProvider.class) {
            if (channels.containsKey(key)) {
                Channel channel = channels.get(key);
                if (channel.isActive()) {
                    return channel;
                } else {
                    channel.close();
                    channels.remove(key);
                }
            }
            // 如channel不存在或者未处于active状态，建立新的channel
            try {
                newChannel = connect(inetSocketAddress, bootstrap);
            } catch (ExecutionException e) {
                logger.error("连接客户端时有错误发生", e);
                return null;
            }
            channels.put(key, newChannel);
        }
        return newChannel;
    }

    private static Channel connect(InetSocketAddress inetSocketAddress, Bootstrap bootstrap) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接{}成功", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 关闭TCP层的心跳探活，改用sRPC实现的应用层心跳探活
                .option(ChannelOption.SO_KEEPALIVE, false)
                // 禁用Nagle算法（该算法的作用是尽可能的发送大数据快，减少网络传输），提高数据传输效率
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

}