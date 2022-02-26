package remoting.transport.server.socket;

import annotation.ServiceScan;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.RpcExceptionBean;
import enumeration.SerializerEnum;
import exception.RpcException;
import factory.SingletonFactory;
import factory.ThreadPoolFactory;
import hook.ShutdownHook;
import registry.service_registry.NacosServiceRegistry;
import registry.service_registry.ServiceRegistry;
import remoting.handler.RequestHandler;
import remoting.transport.AbstractRpcEntity;
import remoting.transport.server.AbstractRpcServer;
import util.ReflectUtil;

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
        this(host, port, serviceRegistry, AbstractRpcEntity.DEFAULT_SERIALIZER, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public SocketServer(String host, int port, ServiceRegistry serviceRegistry, SerializerEnum serializerEnum){
        this(host, port, serviceRegistry, serializerEnum, AbstractRpcEntity.DEFAULT_COMPRESSOR);
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
        ShutdownHook.getShutdownHook().addClearAllHook(registryUtilClass);
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
