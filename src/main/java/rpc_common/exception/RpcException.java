package rpc_common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import rpc_common.enumeration.RpcExceptionBean;

@Data
@AllArgsConstructor
public class RpcException extends RuntimeException{
    private static final long serialVersionUID = 8123785679521392259L;
    private String errorMessage;

    public RpcException(RpcExceptionBean rpcExceptionBean){
        this.errorMessage = rpcExceptionBean.getErrorMessage();
    }
}
