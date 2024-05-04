package top.histevehu.srpc.core.serializer;

/**
 * 通用的（反）序列化接口
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

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
        return switch (code) {
            case 0 -> new KryoSerializer();
            case 1 -> new JsonSerializer();
            case 2 -> new HessianSerializer();
            case 3 -> new ProtobufSerializer();
            default -> null;
        };
    }

}
