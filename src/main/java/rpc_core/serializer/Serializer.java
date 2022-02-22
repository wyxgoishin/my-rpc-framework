package rpc_core.serializer;

import rpc_common.enumeration.SerializerCode;

public interface Serializer {
    SerializerCode DEFAULT_SERIALIZER_CODE = SerializerCode.KRYO;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getByCode(SerializerCode serializerCode){
        if (serializerCode == SerializerCode.KRYO) {
            return new KryoSerializer();
        } else if (serializerCode == SerializerCode.JSON){
            return new JsonSerializer();
        } else {
            return new KryoSerializer();
        }
    }

    static Serializer getByCode(int code){
        if (code == 0){
            return new KryoSerializer();
        } else if (code == 1){
            return new JsonSerializer();
        } else {
            return null;
        }
    }
}
