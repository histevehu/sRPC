package top.histevehu.srpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * sRPC 服务集成Spring的引用注解, 自动注入服务实现
 *
 * @see top.histevehu.srpc.core.spring.ReferenceAnnotationBeanPostProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Component
public @interface SrpcReference {

    String group() default "";

    String name() default "";

    String version() default "";

}
