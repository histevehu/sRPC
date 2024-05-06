package top.histevehu.srpc.core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.util.ReflectUtil;
import top.histevehu.srpc.core.annotation.SrpcService;
import top.histevehu.srpc.core.annotation.SrpcServiceScan;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        // 判断启动类是否注解@SrpcServiceScan以启用服务扫描
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(SrpcServiceScan.class)) {
                logger.info("启动类无@srpcServiceScan注解，服务扫描关闭");
                return;
            }
        } catch (ClassNotFoundException e) {
            logger.error("无法找到启动类");
            throw new RpcException(RpcError.CLASS_NOT_FOUND);
        }
        logger.info("开始扫描服务");
        String basePackage = startClass.getAnnotation(SrpcServiceScan.class).basePackage();
        // 若@SrpcServiceScan注解未定义basePackage属性，则默认将启动类所在包设为basePackage
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 获取basePackage下所有的类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            // 判断类是否注解了@SrpcService以标识其为一个RPC服务提供类
            if (clazz.isAnnotationPresent(SrpcService.class)) {
                String serviceName = clazz.getAnnotation(SrpcService.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建" + clazz + "时发生错误，跳过注册");
                    continue;
                }
                // 若@SrpcService注解未指定serviceName属性，则默认将其所有接口和服务实例对象构成对注册
                // 否则服务实例对象仅注册到指定的serviceName下
                if ("".equals(serviceName)) {
                    // 对象 A 实现了接口 X 和 Y，那么将 A 注册进去后，会有两个服务接口名 X 和 Y 对应于 A 对象
                    // 因此某个接口只能有一个对象提供服务，接口和对象为多对一关系
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        regService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    regService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void regService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
