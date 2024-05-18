package top.histevehu.srpc.core.loadbalance.lb;

import com.alibaba.nacos.api.naming.pojo.Instance;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected Instance doSelect(List<Instance> instances, RpcRequest rpcRequest) {
        return instances.get(new Random().nextInt(instances.size()));
    }

}
