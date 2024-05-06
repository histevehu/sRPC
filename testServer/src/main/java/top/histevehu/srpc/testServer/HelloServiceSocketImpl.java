package top.histevehu.srpc.testServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.HelloObject;
import top.histevehu.srpc.api.HelloService;

public class HelloServiceSocketImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceSocketImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到 helloObject：id={}, message={}", object.getId(), object.getMessage());
        return "sRPC-SocketServer[" + Thread.currentThread().getId() + "]收到消息：id=" + object.getId() + ", message=" + object.getMessage();
    }
}
