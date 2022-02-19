package rpc_core.transport;

import rpc_common.enumeration.SerializerCode;
import rpc_core.serializer.CommonSerializer;

public interface RpcEntity {
    void setSerializer(SerializerCode serializerCode);
    void setSerializer(int serializerCode);
    void setSerializer(CommonSerializer serializer);
}
