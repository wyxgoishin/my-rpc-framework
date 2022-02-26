package rpc_core.remoting.transport.server.socket;

import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_common.factory.ThreadPoolFactory;
import rpc_common.util.ReflectUtil;
import rpc_core.annotation.ServiceScan;
import rpc_core.codec.compressor.Compressor;
import rpc_core.hook.ShutdownHook;
import rpc_core.registry.service_registry.ServiceRegistry;
import rpc_core.remoting.transport.server.AbstractRpcServer;
import rpc_core.codec.serializer.Serializer;
import rpc_core.remoting.handler.RequestHandler;

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

    public SocketServer(String host, int port, ServiceRegistry serviceRegistry){
        this(host, port, serviceRegistry, DEFAULT_SERIALIZER, DEFAULT_COMPRESSOR);
    }

    public SocketServer(String host, int port, ServiceRegistry serviceRegistry, SerializerEnum serializerEnum){
        this(host, port, serviceRegistry, serializerEnum, DEFAULT_COMPRESSOR);
    }

    public SocketServer(String host, int port, ServiceRegistry serviceRegistry, SerializerEnum serializerEnum, CompressorEnum compressorEnum){
        if(serviceRegistry == null){
            throw new RpcException(RpcExceptionBean.SERVICE_REGISTRY_NOT_EXISTS);
        }
        this.host = host;
        this.port = port;
        serializer = Serializer.getByEnum(serializerEnum);
        compressor = Compressor.getByEnum(compressorEnum);
        this.serviceRegistry = serviceRegistry;
        Class<?> bootClass = ReflectUtil.getBootClassByStackTrace();
        if(bootClass.isAnnotationPresent(ServiceScan.class)){
            scanService(bootClass);
        }
    }

    public void start(){
        ShutdownHook.getShutdownHook().addClearAllHook();
        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("server starts succeeded");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                log.info("connect client[{}] succeeded" + socket.getInetAddress().toString());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer, compressor));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("{}", RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage(), e);
        }
    }
}
