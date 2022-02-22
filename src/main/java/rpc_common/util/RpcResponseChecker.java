package rpc_common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

public class RpcResponseChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseChecker.class);
    private static final String SERVICE_NAME = "服务名：";

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if(rpcResponse == null){
            throw new RpcException(RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage() + SERVICE_NAME
                    + rpcRequest.getServiceName() + "(" + rpcRequest.getMethodName() + ")");
        }
        if(!rpcRequest.getRequestId().equals(rpcResponse.getResponseId())){
            throw new RpcException(RpcExceptionBean.RESPONSE_NOT_MATCH.getErrorMessage() + SERVICE_NAME
                    + rpcRequest.getServiceName() + "(" + rpcRequest.getMethodName() + ")");
        }
        if(rpcResponse.getRpcExceptionBean() != null){
            throw new RpcException(rpcResponse.getRpcExceptionBean().getErrorMessage() + SERVICE_NAME
                    + rpcRequest.getServiceName() + "(" + rpcRequest.getMethodName() + ")");
        }
    }
}
