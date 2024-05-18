package top.histevehu.srpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 服务信息实体
 */
@AllArgsConstructor
@Getter
@Builder
@ToString
public class RpcServiceProperties {

    private String serviceName = "";
    /**
     * 当接口有多个实现类时，按组和版本区分
     */
    private String group = "DEFAULT_GROUP";
    private String version = "0";

    public RpcServiceProperties() {
    }

    public RpcServiceProperties(String serviceName) {
        this.serviceName = serviceName;
    }

    public RpcServiceProperties setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public RpcServiceProperties setGroup(String group) {
        this.group = group;
        return this;
    }

    public RpcServiceProperties setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * 输出服务名全称：服务组@服务接口名@版本
     */
    public String toRpcServiceFullName() {
        return this.getGroup() + "@" + this.getServiceName() + "@" + this.getVersion();
    }
}
