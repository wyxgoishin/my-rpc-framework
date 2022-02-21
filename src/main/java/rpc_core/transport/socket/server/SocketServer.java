package rpc_core.transport.socket.server;

import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.factory.SingletonFactory;
import rpc_common.factory.ThreadPoolFactory;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.registry.NacosServiceRegistry;
import rpc_core.hook.ShutdownHook;
import rpc_core.transport.AbstractRpcServer;
import rpc_core.serializer.CommonSerializer;
import rpc_core.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    public SocketServer(String host, int port){
        this(host, port, DEFAULT_SERIALIZER_CODE);
    }

    public SocketServer(String host, int port, SerializerCode serializerCode){
        this(host, port, serializerCode.getCode());
    }

    public SocketServer(String host, int port, int code){
        this.host = host;
        this.port = port;
        serializer = CommonSerializer.getByCode(code);
        threadPool = ThreadPoolFactory.createDefaultThreadPool("rpc-socket-server");
//        serviceProvider = new DefaultServiceProvider();
        serviceRegistry = new NacosServiceRegistry();
        scanService();
    }

    public void start(){
        ShutdownHook.getShutdownHook().addClearAllHook();
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动成功");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接成功，其 IP 为：" + socket.getInetAddress());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("{} : ", RpcExceptionBean.CONNECTION_EXCEPTION, e);
        }
    }
}
