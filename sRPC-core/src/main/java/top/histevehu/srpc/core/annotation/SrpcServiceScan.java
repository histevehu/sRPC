package top.histevehu.srpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用服务扫描注册。注解后将自动扫描basePackage属性指定的基包下所有注解{@link SrpcService}的服务类并注册
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SrpcServiceScan {

    /**
     * 服务扫描的基包，默认为注解标识的启动类所在包
     */
    String basePackage() default "";

}
