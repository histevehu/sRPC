package top.histevehu.srpc.core.spring;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.factory.SingletonFactory;
import top.histevehu.srpc.core.annotation.SrpcService;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.provider.ServiceProviderImpl;
import top.histevehu.srpc.core.registry.NacosServiceRegistry;
import top.histevehu.srpc.core.registry.ServiceRegistry;
import top.histevehu.srpc.core.transport.RpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 在创建 Bean 之前调用以查看类是否已注释
 */
@Component
@Slf4j
public class BeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {


    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;

    public BeanPostProcessor() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        serviceRegistry = SingletonFactory.getInstance(NacosServiceRegistry.class);
    }

    /**
     * 若Bean注解了{@code @}{@link SrpcService}，则将其注册
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(SrpcService.class)) {
            log.info("Bean：{} 注解了 {}", bean.getClass().getName(), SrpcService.class.getCanonicalName());
            SrpcService rpcServiceAnno = bean.getClass().getAnnotation(SrpcService.class);
            RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
            if (!rpcServiceAnno.group().isEmpty()) rpcServiceProperties.setGroup(rpcServiceAnno.group());
            if (!rpcServiceAnno.version().isEmpty()) rpcServiceProperties.setVersion(rpcServiceAnno.version());

            // 若@SrpcService注解未指定serviceName属性，则默认将其所有接口和服务实例对象构成对注册
            // 否则服务实例对象仅注册到指定的serviceName下
            if (rpcServiceAnno.name().isEmpty()) {
                // 对象 A 实现了接口 X 和 Y，那么将 A 注册后，会有两个服务接口名 X 和 Y 对应于 A 对象，服务接口和实现类对象为多对一关系
                // 若要将多个服务实现注册到一个接口下，可通过指定分组实现
                Class<?>[] interfaces = bean.getClass().getInterfaces();
                for (Class<?> oneInterface : interfaces) {
                    rpcServiceProperties.setServiceName(oneInterface.getCanonicalName());
                    regService(bean, rpcServiceProperties);
                }
            } else {
                regService(bean, rpcServiceProperties);
            }
        }
        return bean;
    }

    private <T> void regService(T service, RpcServiceProperties serviceProperties) {
        try {
            serviceProvider.addServiceProvider(service, serviceProperties);
            serviceRegistry.register(serviceProperties.toRpcServiceFullName(), new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcServer.PORT));
        } catch (RpcException e) {
            log.error("{}服务注册发生错误：{}", serviceProperties.toRpcServiceFullName(), e.getMessage());
        } catch (UnknownHostException e) {
            log.error("无法解析Host地址，服务注册失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
