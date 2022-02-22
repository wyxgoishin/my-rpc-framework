package rpc_core.codec.serializer;

import rpc_common.enumeration.SerializerEnum;

public interface Serializer {
    SerializerEnum DEFAULT_SERIALIZER = SerializerEnum.KRYO;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getByCode(int code){
        if (code == SerializerEnum.KRYO.getCode()){
            return new KryoSerializer();
        } else if (code == SerializerEnum.JSON.getCode()){
            return new JsonSerializer();
        } else if (code == SerializerEnum.PROTOBUF.getCode()){
            return new ProtobufSerializer();
        } else if (code == SerializerEnum.HESSIAN.getCode()){
            return new HessianSerializer();
        } else {
            return new KryoSerializer();
        }
    }

    static Serializer getByName(String name){
        if(name == null){
            return new KryoSerializer();
        }else{
            if (name.equals(SerializerEnum.KRYO.getName())){
                return new KryoSerializer();
            } else if (name.equals(SerializerEnum.JSON.getName())){
                return new JsonSerializer();
            } else if (name.equals(SerializerEnum.PROTOBUF.getName())){
                return new ProtobufSerializer();
            } else if (name.equals(SerializerEnum.HESSIAN.getName())){
                return new HessianSerializer();
            } else {
                return new KryoSerializer();
            }
        }
    }

    static Serializer getByEnum(SerializerEnum serializerEnum){
        return serializerEnum == null ? new KryoSerializer() : getByName(serializerEnum.getName());
    }
}
