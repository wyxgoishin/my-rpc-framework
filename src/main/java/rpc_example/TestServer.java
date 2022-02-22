package rpc_example;

import rpc_core.annotation.PropertySource;
import rpc_core.transport.socket.server.SocketServer;
import rpc_example.service.ByeService;
import rpc_example.service.impl.ByeServiceImpl;
import rpc_core.annotation.ServiceScan;
import rpc_core.transport.RpcServer;

// 扫包注册所有带有@Service注解的服务
@ServiceScan("rpc_example.service.impl")
// 以注解方式配置 Server 属性
@PropertySource("server.yaml")
public class TestServer {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9000;
        // RpcServer server = new SocketServer(host, port, serializerCode, compressorCode);
        RpcServer server = new SocketServer();
        // 手动方式注册服务
        ByeService byeService = new ByeServiceImpl();
        server.publishService(byeService, byeService.getClass().getInterfaces()[0].getCanonicalName());
        server.start();
    }
}
