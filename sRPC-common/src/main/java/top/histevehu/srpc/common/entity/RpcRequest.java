package top.histevehu.srpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 客户端向服务端发送的请求对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2994361704629359757L;

    /**
     * 请求号
     */
    private String requestId;

    /**
     * 调用接口名称
     */
    private String interfaceName;

    /**
     * 调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;

    /**
     * 服务所属组名
     */
    private String group;

    /**
     * 服务版本
     */
    private String version;

}
