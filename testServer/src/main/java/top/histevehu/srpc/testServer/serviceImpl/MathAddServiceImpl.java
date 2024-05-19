package top.histevehu.srpc.testServer.serviceImpl;

import top.histevehu.srpc.api.MathAddService;
import top.histevehu.srpc.core.annotation.SrpcService;

@SrpcService
public class MathAddServiceImpl implements MathAddService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
