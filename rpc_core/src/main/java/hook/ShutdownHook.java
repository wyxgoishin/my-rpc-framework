package hook;

import factory.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import util.NacosUtil;
import util.ZkUtil;

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

    public void addClearAllHook(Class<?> clazz){
        log.info("hint: all services will be deregistered automatically when the server is closed");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(clazz == NacosUtil.class){
                NacosUtil.clearRegistry();
            }
            if(clazz == ZkUtil.class){
                ZkUtil.clearRegistry();
            }
            threadPool.shutdown();
        }));
    }
}
