package rpc_core.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.provider.ServiceProvider;
import rpc_core.registry.NacosServiceRegistry;
import rpc_core.transport.AbstractRpcServer;
import rpc_core.transport.RpcServer;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.RequestHandler;
import rpc_core.transport.RequestHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int BLOCKING_QUEUE_SIZE = 100;
    private final RequestHandler requestHandler = new RequestHandler();

    public SocketServer(){
        this(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT);
    }

    public SocketServer(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit){
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                workingQueue, threadFactory);
        serviceProvider = new DefaultServiceProvider();
        serviceRegistry = new NacosServiceRegistry();
    }

    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动成功");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接成功，其 IP 为：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("{} : ", RpcExceptionBean.CONNECTION_EXCEPTION, e);
        }
    }
}
