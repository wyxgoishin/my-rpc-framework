package rpc_common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import rpc_common.enumeration.RpcExceptionBean;

@Data
@AllArgsConstructor
public class RpcException extends RuntimeException{
    private static final long serialVersionUID = 8123785679521392259L;
    private RpcExceptionBean rpcExceptionBean;
}
