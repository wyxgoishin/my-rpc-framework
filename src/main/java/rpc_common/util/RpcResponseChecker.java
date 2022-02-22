package rpc_common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;

public class RpcResponseChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseChecker.class);

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if(rpcResponse == null){
            throw new RpcException("empty response received for service(" + rpcRequest.getServiceName() + "["
                    + rpcRequest.getMethodName() + "])");
        }
        if(!rpcRequest.getRequestId().equals(rpcResponse.getResponseId())){
            throw new RpcException("response pack id not match request pack id");
        }
        if(rpcResponse.getRpcExceptionBean() != null){
            throw new RpcException(rpcResponse.getRpcExceptionBean().getErrorMessage() + "service name: "
                    + rpcRequest.getServiceName() + "(" + rpcRequest.getMethodName() + ")");
        }
    }
}
