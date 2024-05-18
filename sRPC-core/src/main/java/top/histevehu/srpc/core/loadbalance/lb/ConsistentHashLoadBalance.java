package top.histevehu.srpc.core.loadbalance.lb;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.core.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性哈希负载均衡算法
 */
@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected Instance doSelect(List<Instance> serviceAddresses, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        String rpcServiceFullName = rpcRequest.toRpcProperties().toRpcServiceFullName();
        ConsistentHashSelector selector = selectors.get(rpcServiceFullName);
        // 检查更新
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceFullName, new ConsistentHashSelector(serviceAddresses, 100, identityHashCode));
            selector = selectors.get(rpcServiceFullName);
        }
        // 根据服务组、接口名、版本和参数选择实例
        Instance instance = selector.select(rpcServiceFullName + (rpcRequest.getParameters() == null ? "" : Arrays.stream(rpcRequest.getParameters())));
        log.info("一致性哈希负载均衡选择服务实例 {}:{}", instance.getIp(), instance.getPort());
        return instance;
    }

    static class ConsistentHashSelector {

        private final TreeMap<Long, Instance> virtualInvokers;
        private final int identityHashCode;

        ConsistentHashSelector(List<Instance> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (Instance invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker.getIp() + ":" + invoker.getPort() + "#" + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public Instance select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public Instance selectForKey(long hashCode) {
            Map.Entry<Long, Instance> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
