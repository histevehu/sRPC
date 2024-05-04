package top.histevehu.srpc.core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.core.codec.CommonDecoder;
import top.histevehu.srpc.core.codec.CommonEncoder;
import top.histevehu.srpc.core.hook.ShutdownHook;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.provider.ServiceProviderImpl;
import top.histevehu.srpc.core.registry.NacosServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcServer;

import java.net.InetSocketAddress;

/**
 * sRPC 基于Netty的服务端
 */
public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
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

    @Override
    public void start() {
        // BossGroup负责接收客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // WorkerGroup负责网络的读写，业务处理
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ShutdownHook.getShutdownHook().addClearAllHook();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度
                    // 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 禁用Nagle算法（该算法的作用是尽可能的发送大数据快，减少网络传输），提高数据传输效率
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 为worker线程组的SocketChannel添加处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}