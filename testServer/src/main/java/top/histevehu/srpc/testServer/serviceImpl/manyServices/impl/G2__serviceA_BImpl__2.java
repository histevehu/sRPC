package top.histevehu.srpc.testServer.serviceImpl.manyServices.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.manyServices.ServiceA;
import top.histevehu.srpc.api.manyServices.ServiceB;
import top.histevehu.srpc.core.annotation.SrpcService;

@SrpcService(group = "G2", version = "2.0")
public class G2__serviceA_BImpl__2 implements ServiceA, ServiceB {

    private static final Logger logger = LoggerFactory.getLogger(G2__serviceA_BImpl__2.class);

    @Override
    public String hello() {
        return "group: G2, implemented Service: ServiceA+B, version: 2";
    }

    @Override
    public String bye() {
        return "group: G2, implemented Service: ServiceA+B, version: 2";
    }
}
