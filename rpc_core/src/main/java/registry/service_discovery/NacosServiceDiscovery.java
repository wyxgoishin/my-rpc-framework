package registry.service_discovery;

import balancer.LoadBalancer;
import balancer.RoundRobinLoadBalancer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.RpcExceptionBean;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import registry.AbstractRegistry;
import util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery extends AbstractRegistry implements ServiceDiscovery{
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(){
        this(null, null);
    }

    public NacosServiceDiscovery(String serverAddress){
        this(serverAddress, null);
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        this(null, loadBalancer);
    }

    public NacosServiceDiscovery(String serverAddress, LoadBalancer loadBalancer){
        this.serverAddress = serverAddress;
        this.loadBalancer = loadBalancer == null ? new RoundRobinLoadBalancer() : loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getNamingService(serverAddress).getAllInstances(serviceName);
            if(instances.size() == 0){
                log.error(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage() + ":" + serviceName);
                throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage() + ":" + serviceName);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("look up service from nacos failed", e);
        }
        return null;
    }
}
