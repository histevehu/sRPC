package top.histevehu.srpc.core.provider;

/**
 * 保存和提供服务实例对象的接口类
 */
public interface ServiceProvider {
    /**
     * 添加提供服务的实例对象
     */
    <T> ServiceProvider addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 获取提供服务的实例对象
     *
     * @param serviceName 服务名
     */
    Object getServiceProvider(String serviceName);

}