package registry.service_discovery;

import balancer.LoadBalancer;
import balancer.RoundRobinLoadBalancer;
import enumeration.RpcExceptionBean;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.AbstractRegistry;
import util.ZkUtil;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery extends AbstractRegistry implements ServiceDiscovery{
    private final LoadBalancer loadBalancer;

    public ZkServiceDiscovery(){
        this(null, null);
    }

    public ZkServiceDiscovery(String serverAddress){
        this(serverAddress, null);
    }

    public ZkServiceDiscovery(LoadBalancer loadBalancer){
        this(null, loadBalancer);
    }
    public ZkServiceDiscovery(String serverAddress, LoadBalancer loadBalancer){
        this.serverAddress = serverAddress;
        this.loadBalancer = loadBalancer == null ? new RoundRobinLoadBalancer() : loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        CuratorFramework zkClient = ZkUtil.getZkClient(serverAddress);
        List<String> serviceUrls = ZkUtil.getChildNodes(zkClient, serviceName);
        if(serviceUrls == null || serviceUrls.size() == 0){
            throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND + ": " + serviceName);
        }
        String serviceUrl = loadBalancer.select(serviceUrls);
        String[] socketAddress = serviceUrl.split(":");
        return new InetSocketAddress(socketAddress[0], Integer.parseInt(socketAddress[1]));
    }
}
