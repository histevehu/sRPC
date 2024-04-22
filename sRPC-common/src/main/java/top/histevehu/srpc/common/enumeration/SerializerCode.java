package top.histevehu.srpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流中标识序列化和反序列化器枚举类
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    JSON(1);

    private final int code;

}
