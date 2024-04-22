package top.histevehu.srpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据包类型枚举类
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
