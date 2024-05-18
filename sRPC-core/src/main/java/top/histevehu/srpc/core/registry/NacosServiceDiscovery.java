package top.histevehu.srpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.enumeration.LoadBalanceType;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.util.NacosUtil;
import top.histevehu.srpc.core.loadbalance.LoadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos服务发现中心
 */
public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private LoadBalance loadBalance;

    public NacosServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(LoadBalance.LBs[LoadBalanceType.ROUND_ROBIN.getCode()]);
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        try {
            String serviceFullName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                    .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceFullName();
            List<Instance> instances = NacosUtil.getAllInstance(serviceFullName);
            if (instances.isEmpty()) {
                logger.error("找不到对应的服务: {}", serviceFullName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalance.select(instances, rpcRequest);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }

    @Override
    public void setLoadbalance(LoadBalance lb) {
        this.loadBalance = lb;
    }
}
