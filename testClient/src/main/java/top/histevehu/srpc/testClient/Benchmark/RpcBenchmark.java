package top.histevehu.srpc.testClient.Benchmark;

import org.openjdk.jmh.annotations.*;
import top.histevehu.srpc.api.MathAddService;
import top.histevehu.srpc.core.transport.RpcClient;
import top.histevehu.srpc.core.transport.RpcClientProxy;
import top.histevehu.srpc.core.transport.netty.client.NettyClient;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class RpcBenchmark {

    private MathAddService mathAddService;

    @Setup
    public void setup() {
        RpcClient client = new NettyClient();
        RpcClientProxy clientProxy = new RpcClientProxy(client);
        mathAddService = clientProxy.getProxy(MathAddService.class);
    }

    @Benchmark
    public void testRpcCall() {
        int res = mathAddService.add(1, 1);
        assert res == 2;
    }
}
