package rpc_common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rpc_common.enumeration.ResponseStatusBean;
import rpc_common.enumeration.RpcErrorBean;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -526342193122001948L;
    private ResponseStatusBean responseBean;
    private RpcErrorBean rpcErrorBean;
    private T data;

    /*
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(responseStatusBean);
        out.writeObject(data);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.responseStatusBean = (RpcResponseStatusBean) in.readObject();
        this.data = (T) in.readObject();
    }

     */

    public static <K> RpcResponse<K> success(K data){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseBean(ResponseStatusBean.SUCCESS);
        response.setData(data);
        return response;
    }

    public static <K> RpcResponse<K> fail(RpcErrorBean rpcErrorBean){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseBean(ResponseStatusBean.FAIL);
        response.setRpcErrorBean(rpcErrorBean);
        return response;
    }
}
