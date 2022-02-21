package example;

import rpc_core.transport.RpcClient;
import rpc_core.transport.RpcClientProxy;
import rpc_core.transport.netty.client.NettyClient;
import example.service.ByeService;
import example.service.HelloObject;
import example.service.HelloService;

public class TestClient {
    public static void main(String[] args) {
        // RpcClient client = new SocketClient();
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        // 测试一般的服务
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(1, "hello");
        String res = helloService.hello(helloObject);
        System.out.println(res);

        // 测试自定义命名的服务
        rpcClientProxy.setServiceName("bye");
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        res = byeService.bye(Thread.currentThread().getName());
        System.out.println(res);

        // 测试服务名不存在的情况
        rpcClientProxy.setServiceName(null);
        res = byeService.bye(Thread.currentThread().getName());
        System.out.println(res);
    }
}
