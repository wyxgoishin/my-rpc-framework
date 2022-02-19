package rpc_core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service){
        Object result = invokeTargetMethod(rpcRequest, service);
        if(!(result instanceof RpcResponse)){
            logger.info("调用 {} 接口的 {} 方法成功", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        } catch (NoSuchMethodException e){
            return RpcResponse.fail(RpcExceptionBean.METHOD_NOT_FOUND);
        }
        try {
            return method.invoke(service, rpcRequest.getParameters());
        } catch (IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(RpcExceptionBean.RUNTIME_ERROR);
        }
    }
}
