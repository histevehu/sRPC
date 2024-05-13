package top.histevehu.srpc.core.serializer;

import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;
import top.histevehu.srpc.common.extension.ExtensionLoader;
import top.histevehu.srpc.common.extension.SrpcSPI;

import java.util.Arrays;

/**
 * 通用的（反）序列化接口
 */
@SrpcSPI
public interface CommonSerializer {

    String[] SERIALIZERS = {"Kryo", "Json", "Hessian", "Protobuf"};

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 获取（反）序列化器类型编码
     *
     * @return
     */
    int getCode();

    /**
     * 根据序列化反序列化器类型编码获取序列化反序列化器
     *
     * @param code （反）序列化器的编号
     * @return （反）序列化器
     */
    static CommonSerializer getByCode(int code) {
        if (code < SERIALIZERS.length && code >= 0) {
            return ExtensionLoader.getExtensionLoader(CommonSerializer.class).getExtension(SERIALIZERS[code]);
        }
        throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
    }

    static CommonSerializer getByName(String name) {
        if (Arrays.asList(SERIALIZERS).contains(name)) {
            return ExtensionLoader.getExtensionLoader(CommonSerializer.class).getExtension(name);
        }
        throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
    }

}
