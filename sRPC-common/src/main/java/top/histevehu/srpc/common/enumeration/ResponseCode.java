package top.histevehu.srpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法调用的响应状态码枚举类
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "调用方法成功"),
    FAIL(500, "调用方法失败"),
    NOT_FOUND_CLASS(501, "未找到指定类"),
    NOT_FOUND_METHOD(502, "未找到指定方法");


    private final int code;
    private final String message;

}
