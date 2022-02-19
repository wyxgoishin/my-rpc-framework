package rpc_core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.SerializerCode;
import rpc_core.serializer.CommonSerializer;

public class AbstractRpcEntity implements RpcEntity{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected CommonSerializer serializer;
    protected static final SerializerCode DEFAULT_SERIALIZER_CODE = SerializerCode.KRYO;

    @Override
    public void setSerializer(SerializerCode serializerCode) {
        this.serializer = CommonSerializer.getByCode(serializerCode);
    }

    @Override
    public void setSerializer(int serializerCode) {
        this.serializer = CommonSerializer.getByCode(serializerCode);
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
