package rpc_example;

import rpc_core.annotation.PropertySource;
import rpc_core.transport.RpcClient;
import rpc_core.transport.RpcClientProxy;
import rpc_core.transport.netty.client.NettyClient;
import rpc_core.transport.socket.client.SocketClient;
import rpc_example.service.ByeService;
import rpc_example.service.HelloObject;
import rpc_example.service.HelloService;

@PropertySource("client.yaml")
public class TestClient {
    public static void main(String[] args) {
        // RpcClient client = new SocketClient();
        RpcClient client = new SocketClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        // 默认命名的服务
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(1, "hello");
        String res = helloService.hello(helloObject);
        System.out.println(res);

        rpcClientProxy.setServiceName(null);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        res = byeService.bye(Thread.currentThread().getName());
        System.out.println(res);

        // 自定义命名的服务
        rpcClientProxy.setServiceName("bye");
        res = byeService.bye(Thread.currentThread().getName());
        System.out.println(res);
    }
}
