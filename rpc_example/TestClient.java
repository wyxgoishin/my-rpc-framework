package rpc_example;

import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.SerializerEnum;
import rpc_core.annotation.PropertySource;
import rpc_core.balancer.LoadBalancer;
import rpc_core.balancer.RoundRobinLoadBalancer;
import rpc_core.proxy.RpcClientProxy;
import rpc_core.registry.service_discovery.NacosServiceDiscovery;
import rpc_core.registry.service_discovery.ServiceDiscovery;
import rpc_core.registry.service_discovery.ZkServiceDiscovery;
import rpc_core.remoting.transport.client.RpcClient;
import rpc_core.remoting.transport.client.netty.NettyClient;
import rpc_core.remoting.transport.client.socket.SocketClient;
import rpc_example.service.ExampleServiceOne;
import rpc_example.service.ExampleServiceTwo;

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
