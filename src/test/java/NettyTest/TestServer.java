package NettyTest;

import rpc_core.transport.RpcServer;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.provider.ServiceProvider;
import rpc_core.transport.netty.server.NettyServer;
import service.HelloService;
import service.impl.HelloServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer server = new NettyServer("localhost", 9000);
        for(Class<?> clazz : helloService.getClass().getInterfaces()){
            server.publishService(helloService, clazz.getCanonicalName());
        }
        server.start(9000);
    }
}
