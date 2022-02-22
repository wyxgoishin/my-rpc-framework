package rpc_core.transport;

import rpc_common.entity.RpcRequest;

public interface RpcClient extends RpcEntity {
    Object sendRequest(RpcRequest rpcRequest);
}
