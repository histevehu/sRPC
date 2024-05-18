package top.histevehu.srpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RPC错误信息枚举类
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    ILLEGAL_PORT("非法端口"),
    PORT_IN_USE("端口已在使用中"),
    NO_AVAILABLE_PORT("无可用端口"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),

    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现任何接口"),
    SERVICE_EXISTED("当前服务组内服务接口下已经注册有相同版本的服务实现"),

    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),

    SERIALIZER_NOT_FOUND("未设置序列化器"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),

    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    LB_SELECT_FAILED("负载均衡选择实例失败"),

    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类ServiceScan注解缺失"),
    CLASS_NOT_FOUND("找不到类"),

    CONFIG_FILE_NOT_FOUND("未找到配置文件"),
    CONFIG_FILE_PARSE_FAILED("配置文件解析发生错误");


    private final String message;

}
