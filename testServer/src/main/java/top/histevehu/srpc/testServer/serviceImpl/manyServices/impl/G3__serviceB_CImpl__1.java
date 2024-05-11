package top.histevehu.srpc.testServer.serviceImpl.manyServices.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.manyServices.ServiceB;
import top.histevehu.srpc.api.manyServices.ServiceC;
import top.histevehu.srpc.core.annotation.SrpcService;

// 同一个服务组下同一个服务接口的实现只能注册一次
// @SrpcService(group = "G2", version = "1.0")
@SrpcService(group = "G3", version = "1.0")
public class G3__serviceB_CImpl__1 implements ServiceB, ServiceC {

    private static final Logger logger = LoggerFactory.getLogger(G3__serviceB_CImpl__1.class);

    @Override
    public String hello() {
        return "group: G3, implemented Service: ServiceB+C, version: 1";
    }

    @Override
    public String bye() {
        return "group: G3, implemented Service: ServiceB+C, version: 1";
    }
}
