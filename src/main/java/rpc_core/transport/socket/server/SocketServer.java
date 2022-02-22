package rpc_core.transport.socket.server;

import rpc_common.enumeration.CompressorCode;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.factory.SingletonFactory;
import rpc_common.factory.ThreadPoolFactory;
import rpc_core.compresser.Compressor;
import rpc_core.registry.NacosServiceRegistry;
import rpc_core.hook.ShutdownHook;
import rpc_core.transport.AbstractRpcServer;
import rpc_core.serializer.Serializer;
import rpc_core.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("rpc-socket-server");
    private final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    public SocketServer(){
        super();
    }

    public SocketServer(String host, int port){
        this(host, port, DEFAULT_SERIALIZER_CODE.getCode(), DEFAULT_COMPRESSOR_CODE.getCode());
    }

    public SocketServer(String host, int port, SerializerCode serializerCode){
        this(host, port, serializerCode.getCode(), DEFAULT_COMPRESSOR_CODE.getCode());
    }

    public SocketServer(String host, int port, int serializerCode){
        this(host, port, serializerCode, DEFAULT_COMPRESSOR_CODE.getCode());
    }

    public SocketServer(String host, int port, SerializerCode serializerCode, CompressorCode compressorCode){
        this(host, port, serializerCode.getCode(), compressorCode.getCode());
    }

    public SocketServer(String host, int port, int serializerCode, int compressorCode){
        this.host = host;
        this.port = port;
        serializer = Serializer.getByCode(serializerCode);
        compressor = Compressor.getByCode(compressorCode);
//        serviceProvider = new DefaultServiceProvider();
        serviceRegistry = new NacosServiceRegistry();
        scanService();
    }

    public void start(){
        ShutdownHook.getShutdownHook().addClearAllHook();
        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("服务器启动成功");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                log.info("客户端连接成功，其 IP 为：" + socket.getInetAddress());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer, compressor));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("{} : ", RpcExceptionBean.CONNECTION_EXCEPTION, e);
        }
    }
}
