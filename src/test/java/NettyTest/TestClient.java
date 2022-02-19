package NettyTest;

import rpc_core.transport.RpcClient;
import rpc_core.transport.RpcClientProxy;
import rpc_core.transport.netty.client.NettyClient;
import service.HelloObject;
import service.HelloService;

public class TestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(1, "hello");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
