package rpc_example;

import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.SerializerEnum;
import rpc_core.annotation.PropertySource;
import rpc_core.annotation.ServiceScan;
import rpc_core.codec.compressor.Compressor;
import rpc_core.registry.service_registry.NacosServiceRegistry;
import rpc_core.registry.service_registry.ServiceRegistry;
import rpc_core.remoting.transport.server.RpcServer;
import rpc_core.remoting.transport.server.netty.NettyServer;
import rpc_core.remoting.transport.server.socket.SocketServer;
import rpc_example.service.ExampleServiceTwo;
import rpc_example.service.impl.ExampleServiceTwoImpl;

// scan and register service with @Service annotated under given package
@ServiceScan("rpc_example.service.impl")
// use annotation to initialize server from property file
@PropertySource("server.properties")
public class TestServer {
    public static void main(String[] args) {
        /* initialize server explicitly
        String host = "localhost";
        int port = 9000;
        String serverAddress = "127.0.0.1:8848";
        ServiceRegistry serviceRegistry = new NacosServiceRegistry(serverAddress);
        SerializerEnum serializer = SerializerEnum.KRYO;
        CompressorEnum compressor = CompressorEnum.GZIP;
        RpcServer server = new SocketServer(host, port, serviceRegistry, serializer, compressor);
         */

        RpcServer server = new SocketServer();

        // register service explicitly
        ExampleServiceTwo service = new ExampleServiceTwoImpl();
        for(Class<?> clazz : service.getClass().getInterfaces()){
            server.publishService(service, clazz.getCanonicalName());
        }
        server.start();
    }
}
