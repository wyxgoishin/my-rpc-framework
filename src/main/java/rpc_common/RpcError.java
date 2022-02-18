package rpc_common;

import lombok.AllArgsConstructor;
import lombok.Data;
import vo.RpcErrorBean;

@Data
@AllArgsConstructor
public class RpcError extends RuntimeException{
    private static final long serialVersionUID = 8123785679521392259L;
    private RpcErrorBean rpcErrorBean;
}
