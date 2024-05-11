package top.histevehu.srpc.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcResponse;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.enumeration.ResponseCode;
import top.histevehu.srpc.common.factory.SingletonFactory;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * sRPC服务端进行过程调用的处理器
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    /**
     * 调用请求的方法
     */
    public Object handle(RpcRequest rpcRequest) {
        String serviceFullName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceFullName();
        Object service = serviceProvider.getServiceProvider(serviceFullName);
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD, rpcRequest.getRequestId());
        }
        return result;
    }

}
