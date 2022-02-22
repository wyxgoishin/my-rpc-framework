package rpc_core.handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.factory.SingletonFactory;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.provider.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RequestHandler {
//    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider = SingletonFactory.getInstance(DefaultServiceProvider.class);

    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getServiceProvider(rpcRequest.getServiceName());
        Object result = invokeTargetMethod(rpcRequest, service);
        if(!(result instanceof RpcResponse)){
            log.info("调用 {} 服务接口的 {} 方法成功", rpcRequest.getServiceName(), rpcRequest.getMethodName());
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        } catch (NoSuchMethodException e){
            return RpcResponse.fail(RpcExceptionBean.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        try {
            return method.invoke(service, rpcRequest.getParameters());
        } catch (IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(RpcExceptionBean.SERVICE_RUNTIME_EXCEPTION, rpcRequest.getRequestId());
        }
    }
}
