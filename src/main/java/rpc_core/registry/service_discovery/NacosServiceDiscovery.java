package rpc_core.registry.service_discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.balancer.LoadBalancer;
import rpc_common.util.NacosUtil;
import rpc_core.balancer.RoundRobinLoadBalancer;
import rpc_core.registry.AbstractRegistry;

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
