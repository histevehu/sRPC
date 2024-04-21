package top.histevehu.srpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RPC错误信息枚举类
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现任何接口");

    private final String message;

}
