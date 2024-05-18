package top.histevehu.srpc.common.util;

import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;

import java.io.IOException;
import java.net.ServerSocket;

/*
 * IO工具类
 */
@Slf4j
public class IOUtil {

    private static final int PORT_RANGE = 65535;

    /**
     * 检查端口是否被占用
     *
     * @param port 要检查的端口号
     * @return 如果端口被占用返回 true，否则返回 false
     */
    private static boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // 如果端口可用，则 ServerSocket 会成功创建
            return false;
        } catch (IOException e) {
            // 如果创建失败，说明端口被占用
            return true;
        }
    }

    /**
     * 从给定的端口开始，寻找可用的端口
     *
     * @param startPort 起始查找的端口
     * @return 可用端口
     */
    public static int findAvailablePort(int startPort) {
        if (startPort <= 0) {
            throw new RpcException(RpcError.ILLEGAL_PORT);
        }
        int port = startPort;
        while (isPortInUse(port)) {
            port++;
            if (port > PORT_RANGE) {
                throw new RpcException(RpcError.NO_AVAILABLE_PORT);
            }
        }
        log.info("给定端口{}，找到可用端口{}", startPort, port);
        return port;
    }
}
