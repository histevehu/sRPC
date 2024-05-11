package top.histevehu.srpc.core.registry;

import java.net.InetSocketAddress;

/**
 * 注册中心服务注册相关接口
 */
public interface ServiceRegistry {
    /**
     * 向注册中心注册服务端的服务接口 <br/><br/>
     * 注册时注册服务接口及服务提供者的地址和端口，至于接口具体的实现类由服务提供者内的本地服务注册表记录
     * 因此向注册中心注册时不用判断接口是否已经存在
     *
     * @param serviceName       服务名全程（服务组@服务接口@版本）
     * @param inetSocketAddress 提供服务的地址和端口
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
