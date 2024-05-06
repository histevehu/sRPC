package top.histevehu.srpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }

}
