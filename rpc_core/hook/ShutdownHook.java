package rpc_core.hook;

import lombok.extern.slf4j.Slf4j;
import rpc_common.factory.ThreadPoolFactory;
import rpc_common.util.NacosUtil;

import java.util.concurrent.ExecutorService;

/*
shutdown hook for deregister all services automatically when the server is closed
 */
@Slf4j
public class ShutdownHook {
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        log.info("hint: all services will be deregistered automatically when the server is closed");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            threadPool.shutdown();
        }));
    }
}
