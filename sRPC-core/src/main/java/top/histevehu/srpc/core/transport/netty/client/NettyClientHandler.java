package top.histevehu.srpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.factory.SingletonFactory;

import java.net.InetSocketAddress;

/**
 * sRPC 基于Netty的客户端侧处理器
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private final UnprocessedRequests unprocessedRequests;

    private final Bootstrap bootstrap;

    public NettyClientHandler(Bootstrap bootstrap) {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.bootstrap = bootstrap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            unprocessedRequests.complete(msg);
        } finally {
            // 释放入站信息
            // InBound里读取的ByteBuf要手动释放引用计数，避免内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生：{}", cause.getMessage());
        for (StackTraceElement st : cause.getStackTrace()) {
            logger.error(st.toString());
        }
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳检查
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), bootstrap);
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                if (channel != null) {
                    channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
