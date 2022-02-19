package rpc_core.serializer;

public interface CommonSerializer {
    Integer KRYO_SERIALIZER_CODE = 0;
    Integer JSON_SERIALIZER_CODE = 1;
    Integer HESSIAN_SERIALIZER_CODE = 2;
    Integer PROTOBUF_SERIALIZER_CODE = 3;
    Integer DEFAULT_SERIALIZER_CODE = KRYO_SERIALIZER_CODE;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code){
        if (code == KRYO_SERIALIZER_CODE) {
            return new KryoSerializer();
        } else if (code == JSON_SERIALIZER_CODE){
            return new JsonSerializer();
        }else {
            return new KryoSerializer();
        }
    }
}
