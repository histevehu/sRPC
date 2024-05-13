package top.histevehu.srpc.testClient.TestReference;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.histevehu.srpc.core.annotation.SrpcServiceScanSpring;

/*
 * 测试@SrpcReference用客户端
 */
@SrpcServiceScanSpring
public class TestReferenceClient {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestReferenceClient.class);
        ServiceAController serviceAController = (ServiceAController) applicationContext.getBean("serviceAController");
        System.out.println(serviceAController.hello());
    }
}