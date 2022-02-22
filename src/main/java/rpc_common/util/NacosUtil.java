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
//    private static final Logger log = LoggerFactory.getLogger(NacosUtil.class);
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1:8848";
    private static final Set<String> serviceNames = new HashSet<>();
    private static NamingService namingService;
    /*
        因为会通过多进程而非线程形式开启多个 Netty 服务器，所以每个进程中 Netty 服务器对应的 ip:port 是固定的，不存在多线程删不完问题
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
            log.error("{} ：", RpcExceptionBean.CONNECT_NACOS_FAILED.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.CONNECT_NACOS_FAILED);
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
                    log.info("注销服务 {} - {}:{}", serviceName, address.getHostName(), address.getPort());
                } catch (NacosException e) {
                    log.error("{} {}", serviceName, RpcExceptionBean.DEREGISTER_NACOS_INSTANCE_FAILED, e);
                }
            }
            /*  这行也不需要，因为执行完后 JVM 都关了
                serviceNames.clear();
             */
        }
    }
}
