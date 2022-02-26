package rpc_common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_core.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NacosUtil {
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1:8848";
    private static final Set<String> serviceNames = new HashSet<>();
    private static NamingService namingService;
    /*
    As multi servers will be opened in fashion of multi progress, thus the ip and host of corresponding server is static
     */
    private static InetSocketAddress address;

    public static NamingService getNamingService(String serverAddress){
        if(namingService != null){
            return namingService;
        }
        serverAddress = serverAddress == null ? DEFAULT_SERVER_ADDRESS : serverAddress;
        try {
            namingService = NamingFactory.createNamingService(serverAddress);
            return namingService;
        } catch (NacosException e) {
            log.error("get nacos naming service failed", e);
            throw new RuntimeException("get nacos naming service failed");
        }
    }

    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        serviceNames.add(serviceName);
        address = inetSocketAddress;
    }

    public static void clearRegistry(){
        if(!serviceNames.isEmpty() && address != null){
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, address.getHostName(), address.getPort());
                    log.info("deregister service {} - {}:{}", serviceName, address.getHostName(), address.getPort());
                } catch (NacosException e) {
                    log.error("deregister {} from nacos failed", serviceName, e);
                }
            }
        }
    }
}
