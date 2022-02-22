package rpc_core.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rpc_common.enumeration.ResponseStatusBean;
import rpc_common.enumeration.RpcExceptionBean;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -526342193122001948L;
    private ResponseStatusBean responseBean;
    private RpcExceptionBean rpcExceptionBean;
    private T data;
    private String responseId;

    public static <K> RpcResponse<K> success(K data, String requestId){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseBean(ResponseStatusBean.SUCCESS);
        response.setData(data);
        response.setResponseId(requestId);
        return response;
    }

    public static <K> RpcResponse<K> fail(RpcExceptionBean rpcExceptionBean, String requestId){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseBean(ResponseStatusBean.FAIL);
        response.setRpcExceptionBean(rpcExceptionBean);
        response.setResponseId(requestId);
        return response;
    }
}
