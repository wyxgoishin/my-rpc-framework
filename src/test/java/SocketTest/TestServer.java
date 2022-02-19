package SocketTest;

import rpc_core.RpcServer;
import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;
import rpc_core.transport.socket.server.SocketServer;
import service.HelloService;
import service.impl.HelloServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
