package rpc_core.registry;


import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.util.NacosUtil;

import java.net.InetSocketAddress;

public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("{} : ", RpcExceptionBean.REGISTER_SERVICE_TO_NACOS_FAILED, e);
        }
    }

}
