package rpc_common.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolFactory {
//    private final static Logger log = LoggerFactory.getLogger(ThreadPoolFactory.class);
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private static final Map<String, ExecutorService> threadPollsMap = new ConcurrentHashMap<>();

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPollsMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPollsMap.put(threadNamePrefix, pool);
        }
        return pool;

    }

    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAll() {
        log.info("关闭所有线程池...");
        threadPollsMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("关闭线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                log.error("关闭线程池失败！");
                executorService.shutdownNow();
            }
        });
    }
}
