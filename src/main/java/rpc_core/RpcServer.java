package rpc_core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public RpcServer(){
        this(CORE_POOL_SIZE);
    }

    public RpcServer(int corePoolSize){
        this(corePoolSize, MAXIMUM_POOL_SIZE);
    }

    public RpcServer(int corePoolSize, int maximumPoolSize){
        this(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TIME_UNIT);
    }

    public RpcServer(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit){
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                workingQueue, threadFactory);
    }
}
