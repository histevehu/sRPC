package top.histevehu.srpc.core.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.histevehu.srpc.common.factory.ThreadPoolFactory;
import top.histevehu.srpc.common.util.NacosUtil;

public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("sRPC关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
