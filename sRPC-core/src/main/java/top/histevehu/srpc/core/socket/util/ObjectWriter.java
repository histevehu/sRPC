package top.histevehu.srpc.core.socket.util;

import top.histevehu.srpc.common.entity.RpcRequest;
import top.histevehu.srpc.common.enumeration.PackageType;
import top.histevehu.srpc.core.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * sRPC Socket序列化并输出字节流
 */
public class ObjectWriter {

    private static final int MAGIC_NUMBER = 0x981017;

    /**
     * 序列化对象并输出字节流
     *
     * @param outputStream 输出流
     * @param object       对象
     * @param serializer   序列化器
     */
    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException {

        outputStream.write(intTo4Bytes(MAGIC_NUMBER));
        if (object instanceof RpcRequest) {
            outputStream.write(intTo4Bytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intTo4Bytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(intTo4Bytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        outputStream.write(intTo4Bytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();

    }

    /**
     * 将一个整数转换为一个长度为4的字节数组（大端序）
     */
    private static byte[] intTo4Bytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
