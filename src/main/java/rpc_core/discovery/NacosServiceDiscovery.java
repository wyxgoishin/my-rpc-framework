package rpc_core.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.balancer.LoadBalancer;
import rpc_core.balancer.RandomLoadBalancer;
import rpc_common.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{
//    private static final Logger log = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(){
        this(null);
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if(loadBalancer == null){
            this.loadBalancer = new RandomLoadBalancer();
        }else{
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getNamingService().getAllInstances(serviceName);
            if(instances.size() == 0){
                log.error(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage() + serviceName);
                throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage() + serviceName);
            }
            /*
            负载均衡
             */
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("{} : ", RpcExceptionBean.LOOKUP_SERVICE_IN_NACOS_FAILED, e);
        }
        return null;
    }
}
