package rpc_core.transport;

import rpc_common.enumeration.SerializerCode;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.RpcEntity;

public interface RpcServer extends RpcEntity {
    void start(int port);
    <T> void publishService(Object service, String serviceName);
}
