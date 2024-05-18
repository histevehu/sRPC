package top.histevehu.srpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.util.NacosUtil;

import java.net.InetSocketAddress;

/**
 * Nacos服务注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceFullName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceFullName, inetSocketAddress);
            logger.info("服务中心注册：服务接口 {} 服务提供实例 {}",
                    serviceFullName,
                    inetSocketAddress.getAddress());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}

