package top.histevehu.srpc.core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.core.handler.RequestHandler;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.socket.util.ObjectReader;
import top.histevehu.srpc.core.transport.socket.util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * sRPC 基于Socket的服务端的处理远程方法的工作线程
 */
public class SocketServerHandlerRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SocketServerHandlerRunnable.class);

    private final Socket socket;
    private final RequestHandler requestHandler;
    private final CommonSerializer serializer;

    public SocketServerHandlerRunnable(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
