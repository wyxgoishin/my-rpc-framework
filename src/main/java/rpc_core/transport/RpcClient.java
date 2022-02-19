package rpc_core.transport;

import rpc_common.entity.RpcRequest;
import rpc_common.enumeration.SerializerCode;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.RpcEntity;

public interface RpcClient extends RpcEntity {
    Object sendRequest(RpcRequest rpcRequest);
}
