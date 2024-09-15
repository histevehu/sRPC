package top.histevehu.srpc.core.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;
import top.histevehu.srpc.core.annotation.SrpcService;
import top.histevehu.srpc.core.annotation.SrpcServiceScanSpring;

/**
 * 扫描和过滤指定的注解
 */
@Slf4j
public class BeanCustomScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE = "top.histevehu.srpc.core.spring";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取@SrpcServiceScanSpring注解的属性和值
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(SrpcServiceScanSpring.class.getName()));
        String rpcScanBasePackages = "";
        if (rpcScanAnnotationAttributes != null) {
            // get the value of the basePackage property
            rpcScanBasePackages = rpcScanAnnotationAttributes.getString(BASE_PACKAGE_ATTRIBUTE_NAME);
        } else {
            rpcScanBasePackages = ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName();
        }
        // 扫描 @SrpcService 注解
        BeanCustomScanner rpcServiceScanner = new BeanCustomScanner(beanDefinitionRegistry, SrpcService.class);
        // 扫描 @Component 注解
        BeanCustomScanner springBeanScanner = new BeanCustomScanner(beanDefinitionRegistry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
        int scanCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", scanCount);
        // 注册 ReferenceAnnotationBeanPostProcessor 到 Spring 容器
        beanDefinitionRegistry.registerBeanDefinition(ReferenceAnnotationBeanPostProcessor.BEAN_NAME, new RootBeanDefinition(ReferenceAnnotationBeanPostProcessor.class));
    }

}
