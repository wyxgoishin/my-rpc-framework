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

// 扫包注册所有带有@Service注解的服务
@ServiceScan("rpc_example.service.impl")
// 以注解方式配置 Server 属性
@PropertySource("server.properties")
public class TestServer {
    public static void main(String[] args) {
        /* 手动初始化 Server 的属性
        String host = "localhost";
        int port = 9000;
        String serverAddress = "127.0.0.1:8848";
        ServiceRegistry serviceRegistry = new NacosServiceRegistry(serverAddress);
        SerializerEnum serializer = SerializerEnum.KRYO;
        CompressorEnum compressor = CompressorEnum.GZIP;
        RpcServer server = new SocketServer(host, port, serviceRegistry, serializer, compressor);
        RpcServer server = new SocketServer();
         */

        RpcServer server = new SocketServer();

        // 手动方式注册服务
        ExampleServiceTwo service = new ExampleServiceTwoImpl();
        for(Class<?> clazz : service.getClass().getInterfaces()){
            server.publishService(service, clazz.getCanonicalName());
        }
        server.start();
    }
}
