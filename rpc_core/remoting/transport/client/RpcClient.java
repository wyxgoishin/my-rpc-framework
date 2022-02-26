package rpc_core.remoting.transport.client;

import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.transport.RpcEntity;

public interface RpcClient extends RpcEntity {
    Object sendRequest(RpcRequest rpcRequest);
}
