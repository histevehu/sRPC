package top.histevehu.srpc.testServer.serviceImpl.manyServices.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.manyServices.ServiceA;
import top.histevehu.srpc.api.manyServices.ServiceC;
import top.histevehu.srpc.core.annotation.SrpcService;

@SrpcService
public class serviceA_CImp implements ServiceA, ServiceC {

    private static final Logger logger = LoggerFactory.getLogger(serviceA_CImp.class);

    @Override
    public String hello() {
        return "group: DEFAULT_GROUP, implemented Service: ServiceA+C, version: DEFAULT_VERSION";
    }
}
