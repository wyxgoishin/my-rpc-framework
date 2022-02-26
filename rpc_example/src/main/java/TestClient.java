import annotation.PropertySource;
import proxy.RpcClientProxy;
import remoting.transport.client.RpcClient;
import remoting.transport.client.netty.NettyClient;
import service.ExampleServiceOne;
import service.ExampleServiceTwo;

// use annotation to initialize client from property file
@PropertySource("client.yaml")
public class TestClient {
    public static void main(String[] args) {
        /* initialize client explicitly
        String serviceAddress = "127.0.0.1:8848";
        LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        ServiceDiscovery serviceDiscovery = new NacosServiceDiscovery(serviceAddress, loadBalancer);
        SerializerEnum serializer = SerializerEnum.KRYO;
        CompressorEnum compressor = CompressorEnum.GZIP;
        RpcClient client = new SocketClient(serviceDiscovery, serializer, compressor);
         */

        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        // service with default name
        ExampleServiceOne serviceOne = rpcClientProxy.getProxy(ExampleServiceOne.class);
        String replyOne = serviceOne.doService();
        System.out.println(replyOne);

        ExampleServiceTwo serviceTwo = rpcClientProxy.getProxy(ExampleServiceTwo.class);
        String replyTwo = serviceTwo.doService("client: service two");
        System.out.println(replyTwo);

        // service with custom name
        rpcClientProxy.setServiceName("service_two");
        String replyThree = serviceTwo.doService("client: service three");
        System.out.println(replyThree);
    }
}
