package top.histevehu.srpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * sRPC服务注解，标识在服务的实现类上。<br/><br/>
 * 若要实现sRPC服务的自动扫描注册，在需要的服务实现类上注解{@link SrpcService}，并在启动类上注解{@link SrpcServiceScan}，设置正确的扫描基包。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface SrpcService {

    String group() default "";

    String name() default "";

    String version() default "";

}
