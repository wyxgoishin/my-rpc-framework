package rpc_core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

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
}
