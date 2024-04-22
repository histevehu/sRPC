package top.histevehu.srpc.core.serializer;

/**
 * 通用的（反）序列化接口
 */
public interface CommonSerializer {

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
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            default:
                return null;
        }
    }

}
