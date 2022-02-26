package registry.service_registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import registry.AbstractRegistry;
import util.NacosUtil;

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
            log.error("register service to nacos failed", e);
        }
    }

}
