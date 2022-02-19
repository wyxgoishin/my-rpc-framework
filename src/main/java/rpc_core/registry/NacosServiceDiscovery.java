package rpc_core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getNamingService().getAllInstances(serviceName);
            if(instances.size() == 0){
                logger.error(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage());
                throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND);
            }
            /*
            这里涉及负载均衡问题，为了简化先取第 1 个
             */
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("{} : ", RpcExceptionBean.LOOKUP_SERVICE_IN_NACOS_FAILED, e);
        }
        return null;
    }
}
