package top.histevehu.srpc.core.transport.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.enumeration.ResponseCode;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.util.RpcMessageChecker;
import top.histevehu.srpc.core.loadbalance.LoadBalance;
import top.histevehu.srpc.core.loadbalance.lb.RoundRobinLoadBalance;
import top.histevehu.srpc.core.registry.ServiceDiscovery;
import top.histevehu.srpc.core.serializer.CommonSerializer;
import top.histevehu.srpc.core.transport.RpcClient;
import top.histevehu.srpc.core.transport.socket.util.ObjectReader;
import top.histevehu.srpc.core.transport.socket.util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * sRPC基于Socket的客户端
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceDiscovery serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("Nacos");

    private final CommonSerializer serializer;

    public SocketClient() {
        this(DEFAULT_SERIALIZER, new RoundRobinLoadBalance());
    }

    public SocketClient(LoadBalance loadBalance) {
        this(DEFAULT_SERIALIZER, loadBalance);
    }

    public SocketClient(Integer serializer) {
        this(serializer, new RoundRobinLoadBalance());
    }

    public SocketClient(Integer serializer, LoadBalance loadBalance) {
        this.serviceDiscovery.setLoadbalance(loadBalance);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public RpcResponse<Object> sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse<Object> rpcResponse = (RpcResponse<Object>) obj;
            if (rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse;
        } catch (IOException e) {
            logger.error("sRPC调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

}
