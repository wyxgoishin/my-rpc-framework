import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;
import rpc_core.server.RpcServer;
import service.HelloService;
import service.impl.HelloServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
