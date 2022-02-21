package rpc_common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final Set<String> serviceNames = new HashSet<>();
    private static final NamingService namingService;
    /*
        因为会通过多进程而非线程形式开启多个 Netty 服务器，所以每个进程中 Netty 服务器对应的 ip:port 是固定的，不存在多线程删不完问题
     */
    private static InetSocketAddress address;

    static {
        namingService = getNamingService();
    }

    public static NamingService getNamingService(){
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("{} ：", RpcExceptionBean.CONNECT_NACOS_FAILED.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.CONNECT_NACOS_FAILED);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        serviceNames.add(serviceName);
        address = inetSocketAddress;
    }

    public static void clearRegistry(){
        if(!serviceNames.isEmpty() && address != null){
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, address.getHostName(), address.getPort());
                    logger.info("注销服务 {} - {}:{}", serviceName, address.getHostName(), address.getPort());
                } catch (NacosException e) {
                    logger.error("{} {}", serviceName, RpcExceptionBean.DEREGISTER_NACOS_INSTANCE_FAILED, e);
                }
            }
            /*  这行也不需要，因为执行完后 JVM 都关了
                serviceNames.clear();
             */
        }
    }
}
