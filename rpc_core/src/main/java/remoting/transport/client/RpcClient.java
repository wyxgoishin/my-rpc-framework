package remoting.transport.client;

import remoting.dto.RpcRequest;
import remoting.transport.RpcEntity;

public interface RpcClient extends RpcEntity {
    Object sendRequest(RpcRequest rpcRequest);
}
