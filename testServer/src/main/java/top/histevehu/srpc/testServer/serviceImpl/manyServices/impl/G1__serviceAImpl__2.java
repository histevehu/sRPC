package top.histevehu.srpc.testServer.serviceImpl.manyServices.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.manyServices.ServiceA;
import top.histevehu.srpc.core.annotation.SrpcService;

@SrpcService(group = "G1", version = "2.0")
public class G1__serviceAImpl__2 implements ServiceA {

    private static final Logger logger = LoggerFactory.getLogger(G1__serviceAImpl__2.class);

    @Override
    public String hello() {
        return "group: G1, implemented Service: ServiceA, version: 2";
    }
}
