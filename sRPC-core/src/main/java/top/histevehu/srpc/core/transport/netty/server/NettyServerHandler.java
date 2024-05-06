package top.histevehu.srpc.core.transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.factory.ThreadPoolFactory;
import top.histevehu.srpc.core.handler.RequestHandler;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;


/**
 * sRPC 基于Netty的服务端侧处理器
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "sRPC-NettyServer";
    private static final ExecutorService threadPool;

    static {
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try {
                if (msg.getHeartBeat()) {
                    logger.info("接收到客户端{}心跳包", ctx.channel().remoteAddress());
                    return;
                }
                logger.info("sRPC服务器接收到请求: {}", msg);
                Object result = requestHandler.handle(msg);
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
                } else {
                    logger.error("通道不可写");
                }
            } finally {
                // InBound里读取的ByteBuf要手动释放引用计数，避免内存泄漏
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switch (cause) {
            case SocketException se -> {
                logger.error("与客户端{}连接发生错误：{}", ctx.channel().remoteAddress(), cause.getMessage());
            }
            default -> {
                logger.error("处理过程调用时有错误发生：{}", cause.getMessage());
            }
        }
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到客户端{}心跳包，断开连接", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
