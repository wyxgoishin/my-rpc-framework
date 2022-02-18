package rpc_core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Externalizable {
    private static final long serialVersionUID = -526342193122001948L;
    private RpcResponseStatusBean responseStatusBean;
    private T data;

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

    public static <K> RpcResponse<K> success(K data){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseStatusBean(RpcResponseStatusBean.SUCCESS);
        response.setData(data);
        return response;
    }

    public static <K> RpcResponse<K> serviceNotExists(K data){
        RpcResponse<K> response = new RpcResponse<>();
        response.setResponseStatusBean(RpcResponseStatusBean.SERVICE_NOT_EXISTS);
        response.setData(data);
        return response;
    }
}
