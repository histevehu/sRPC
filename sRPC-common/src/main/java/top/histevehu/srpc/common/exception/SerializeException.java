package top.histevehu.srpc.common.exception;

import java.io.Serial;

/*
 *  序列化异常
 */
public class SerializeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3907497888436900366L;

    public SerializeException(String msg) {
        super(msg);
    }
}
