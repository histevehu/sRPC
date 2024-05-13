package top.histevehu.srpc.core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import top.histevehu.srpc.core.codec.CommonDecoder;
import top.histevehu.srpc.core.codec.CommonEncoder;
import top.histevehu.srpc.core.hook.ShutdownHook;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.AbstractRpcServer;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * sRPC 基于Netty的服务端
 */
public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer() {
        this(DEFAULT_SERIALIZER);
    }

    public NettyServer(Integer serializer) {
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @SneakyThrows
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
                    // 关闭TCP层的心跳探活，改用sRPC实现的应用层心跳探活
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    // 禁用Nagle算法（该算法的作用是尽可能的发送大数据快，减少网络传输），提高数据传输效率
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 为worker线程组的SocketChannel添加处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // ========== outBoundHandler部分 ==========
                            pipeline.addLast(new CommonEncoder(serializer))
                                    // ========== inBoundHandler部分 ==========
                                    // 服务端心跳检查，若30秒内该链上都没有读到数据，就会调用userEventTriggered方法
                                    .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(InetAddress.getLocalHost().getHostAddress(), PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}