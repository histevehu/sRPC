package top.histevehu.srpc.testServer.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;

// @SrpcService
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到 helloObject：id={}, message={}", object.getId(), object.getMessage());
        return "收到消息：id=" + object.getId() + ", message=" + object.getMessage();
    }
}
