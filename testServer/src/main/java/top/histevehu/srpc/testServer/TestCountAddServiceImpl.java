package top.histevehu.srpc.testServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.api.TestCountAddObject;
import top.histevehu.srpc.api.TestCountAddService;
import top.histevehu.srpc.core.annotation.SrpcService;

@SrpcService
public class TestCountAddServiceImpl implements TestCountAddService {

    private static final Logger logger = LoggerFactory.getLogger(TestCountAddServiceImpl.class);


    @Override
    public String countAdd(TestCountAddObject object) {
        logger.info("接收到 TestCountAddObject：num={}, offset={}", object.getNum(), object.getOffset());
        return object.getNum() + "+" + object.getOffset() + "=" + (object.getNum() + object.getOffset());
    }
}
