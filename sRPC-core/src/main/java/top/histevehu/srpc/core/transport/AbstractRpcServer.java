package top.histevehu.srpc.core.transport;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.entity.RpcServiceProperties;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.factory.SingletonFactory;
import top.histevehu.srpc.common.util.IOUtil;
import top.histevehu.srpc.common.util.ReflectUtil;
import top.histevehu.srpc.common.util.config.ConfigMap;
import top.histevehu.srpc.core.annotation.SrpcService;
import top.histevehu.srpc.core.annotation.SrpcServiceScan;
import top.histevehu.srpc.core.provider.ServiceProvider;
import top.histevehu.srpc.core.provider.ServiceProviderImpl;
import top.histevehu.srpc.core.registry.ServiceRegistry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

import static top.histevehu.srpc.common.util.config.ConfigFileUtil.parseConfigFile;

@Slf4j
public abstract class AbstractRpcServer implements RpcServer {

    @Getter
    private static int port;

    static {
        try {
            ConfigMap configMap = parseConfigFile("META-INF/config/sRPC-Server");
            // 读取配置端口
            int startPort = configMap.get("port")
                    .map(Integer::parseInt)
                    .orElseThrow(() -> new RpcException(RpcError.CONFIG_FILE_PARSE_FAILED, "Netty Server配置文件中缺少端口"));
            port = IOUtil.findAvailablePort(startPort);
        } catch (IOException e) {
            log.error("读取Netty Server配置文件时发生错误：", e);
            System.exit(-1);
        }
    }

    protected static ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("Nacos");
    protected static ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    /**
     * 若启动类使用了{@code @SrpcServiceScan}注解，则将自动调用本方法扫描基包下所有注解{@code @SrpcService}标识的服务并注册
     */
    public void scanServices(int port) {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        // 判断启动类是否注解@SrpcServiceScan以启用服务扫描
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(SrpcServiceScan.class)) {
                log.info("启动类无@srpcServiceScan注解");
                return;
            }
        } catch (ClassNotFoundException e) {
            log.error("无法找到启动类");
            throw new RpcException(RpcError.CLASS_NOT_FOUND);
        }
        log.info("启动类有@srpcServiceScan注解");
        String basePackage = startClass.getAnnotation(SrpcServiceScan.class).basePackage();
        // 若@SrpcServiceScan注解未定义basePackage属性，则默认将启动类所在包设为basePackage
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 获取basePackage下所有的类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            // 判断类是否注解了@SrpcService以标识其为一个RPC服务实现类
            if (clazz.isAnnotationPresent(SrpcService.class)) {
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建" + clazz + "时发生错误，跳过注册");
                    continue;
                }
                // 从@SrpcService注解中获取服务名称、版本号、分组等信息
                SrpcService annotations = clazz.getAnnotation(SrpcService.class);
                RpcServiceProperties serviceProperties = new RpcServiceProperties(annotations.name());
                if (!annotations.group().isEmpty()) serviceProperties.setGroup(annotations.group());
                if (!annotations.version().isEmpty()) serviceProperties.setVersion(annotations.version());
                // 若@SrpcService注解未指定serviceName属性，则默认将其所有接口和服务实例对象构成对注册
                // 否则服务实例对象仅注册到指定的serviceName下
                if (serviceProperties.getServiceName().isEmpty()) {
                    // 对象 A 实现了接口 X 和 Y，那么将 A 注册后，会有两个服务接口名 X 和 Y 对应于 A 对象，服务接口和实现类对象为多对一关系
                    // 若要将多个服务实现注册到一个接口下，可通过指定分组实现
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        serviceProperties.setServiceName(oneInterface.getCanonicalName());
                        regService(obj, serviceProperties);
                    }
                } else {
                    regService(obj, serviceProperties);
                }
            }
        }
    }

    /**
     * 向服务端本地服务注册表和注册中心注册
     *
     * @param service           服务实现类
     * @param serviceProperties 服务信息
     */
    @Override
    public <T> void regService(T service, RpcServiceProperties serviceProperties) {
        try {
            serviceProvider.addServiceProvider(service, serviceProperties);
            serviceRegistry.register(serviceProperties.toRpcServiceFullName(), new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port));
        } catch (RpcException e) {
            log.error("sRPC {}服务注册发生错误：{}", serviceProperties.toRpcServiceFullName(), e.getMessage());
        } catch (UnknownHostException e) {
            log.error("无法解析Host地址，服务注册失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
