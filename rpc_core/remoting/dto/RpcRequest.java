package rpc_core.remoting.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -1957686175929610806L;
    /*
    default service name is the canonical name of the interface name of the service
     */
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String requestId;
    private boolean isHeartBeat;
}
