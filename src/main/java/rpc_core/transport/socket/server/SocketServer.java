package rpc_core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.registry.ServiceRegistry;
import rpc_core.RpcServer;
import rpc_core.transport.RequestHandler;
import rpc_core.transport.RequestHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int BLOCKING_QUEUE_SIZE = 100;
    private final RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    public SocketServer(ServiceRegistry serviceRegistry){
        this(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, serviceRegistry);
    }

    public SocketServer(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, ServiceRegistry serviceRegistry){
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                workingQueue, threadFactory);
        this.serviceRegistry = serviceRegistry;
    }

    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动成功");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接成功，其 IP 为：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时发生错误：" + e);
        }
    }
}
