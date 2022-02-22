package rpc_core.registry.service_registry;


import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.util.NacosUtil;
import rpc_core.registry.AbstractRegistry;

import java.net.InetSocketAddress;

@Slf4j
@AllArgsConstructor
public class NacosServiceRegistry extends AbstractRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NamingService namingService = NacosUtil.getNamingService(serverAddress);
            NacosUtil.registerService(namingService, serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("{} : ", RpcExceptionBean.REGISTER_SERVICE_TO_NACOS_FAILED, e);
        }
    }

}
