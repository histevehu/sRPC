package top.histevehu.srpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个sRPC服务提供的实现类。<br/><br/>
 * 若要实现sRPC服务的自动扫描注册，在需要的服务实现类上注解{@link SrpcService}，并在启动类上注解{@link SrpcServiceScan}，设置正确的扫描基包。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SrpcService {

    String name() default "";

}
