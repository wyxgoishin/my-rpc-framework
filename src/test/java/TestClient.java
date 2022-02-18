import rpc_core.client.RpcClientProxy;
import service.HelloObject;
import service.HelloService;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("localhost", 9000);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(1, "hello");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
