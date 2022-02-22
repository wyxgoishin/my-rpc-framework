package rpc_core.provider;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.exception.RpcException;
import rpc_common.enumeration.RpcExceptionBean;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultServiceProvider implements ServiceProvider {

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredServices = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void addServiceProvider(T service, String serviceName) {
        if(registeredServices.contains(serviceName)) return;
        registeredServices.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("try to register service: {}", serviceName);
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND.getErrorMessage() + ": " + serviceName);
        }
        return service;
    }
}
