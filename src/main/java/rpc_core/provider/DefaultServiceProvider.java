package rpc_core.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.exception.RpcException;
import rpc_common.enumeration.RpcExceptionBean;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceProvider.class);

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredServices = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void addServiceProvider(T service, String serviceName) {
        if(registeredServices.contains(serviceName)) return;
        registeredServices.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcExceptionBean.SERVICE_NOT_IMPLEMENTS_ANY_INTERFACES);
        }
        for(Class<?> i : interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口：{} 注册服务：{}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
