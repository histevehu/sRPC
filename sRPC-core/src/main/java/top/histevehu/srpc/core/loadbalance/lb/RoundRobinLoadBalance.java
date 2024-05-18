package top.histevehu.srpc.core.loadbalance.lb;

import com.alibaba.nacos.api.naming.pojo.Instance;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * 轮询负载均衡
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected Instance doSelect(List<Instance> instances, RpcRequest rpcRequest) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }

}
