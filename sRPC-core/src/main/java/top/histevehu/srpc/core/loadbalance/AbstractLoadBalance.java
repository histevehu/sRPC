package top.histevehu.srpc.core.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.entity.RpcRequest;

import java.util.List;

@Slf4j
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public Instance select(List<Instance> instances, RpcRequest rpcRequest) {
        Instance instance;
        if (instances == null || instances.isEmpty()) {
            return null;
        } else if (instances.size() == 1) {
            instance = instances.getFirst();
            log.info("仅发现一个服务提供实例：{}:{}", instance.getIp(), instance.getPort());
        } else {
            instance = doSelect(instances, rpcRequest);
            log.info("负载均衡选择服务提供实例：{}:{}", instance.getIp(), instance.getPort());
        }
        return instance;
    }

    protected abstract Instance doSelect(List<Instance> instances, RpcRequest rpcRequest);

}
