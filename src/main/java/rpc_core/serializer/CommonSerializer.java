package rpc_core.serializer;

public interface CommonSerializer {
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code){
        if (code == 1) {
            return new JsonSerializer();
        }
        return null;
    }
}
