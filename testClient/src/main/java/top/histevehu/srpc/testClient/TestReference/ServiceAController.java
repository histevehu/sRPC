package top.histevehu.srpc.testClient.TestReference;

import org.springframework.stereotype.Component;
import top.histevehu.srpc.api.manyServices.ServiceA;
import top.histevehu.srpc.core.annotation.SrpcReference;

@Component
public class ServiceAController {
    @SrpcReference(group = "G1", version = "1.0")
    private ServiceA serviceA;

    public String hello() {
        return serviceA.hello();
    }
}
