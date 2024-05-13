package top.histevehu.srpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.util.NacosUtil;
import top.histevehu.srpc.core.loadbalancer.LoadBalancer;
import top.histevehu.srpc.core.loadbalancer.RoundRobinLoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos服务发现中心
 */
public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this(new RoundRobinLoadBalancer());
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) this.loadBalancer = new RoundRobinLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceFullName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceFullName);
            if (instances.isEmpty()) {
                logger.error("找不到对应的服务: {}", serviceFullName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }

    @Override
    public void setLoadbalance(LoadBalancer lb) {
        this.loadBalancer = lb;
    }
}
