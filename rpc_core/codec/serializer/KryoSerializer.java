package rpc_core.codec.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;
import rpc_common.exception.RpcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            try (Output output = new Output(byteArrayOutputStream)){
                Kryo kryo = kryoThreadLocal.get();
                kryo.writeObject(output, obj);
                kryoThreadLocal.remove();
                return output.toBytes();
            }
        } catch (IOException e) {
            log.error("error occurred during serializing using kryo serializer");
            throw new RuntimeException("error occurred during serializing using kryo serializer");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            try (Input input = new Input(byteArrayInputStream)) {
                Kryo kryo = kryoThreadLocal.get();
                Object o = kryo.readObject(input, clazz);
                kryoThreadLocal.remove();
                return o;
            }
        } catch (IOException e) {
            log.error("error occurred during serializing using kryo serializer");
            throw new RuntimeException("error occurred during serializing using kryo serializer");
        }
    }

    @Override
    public int getCode() {
        return SerializerEnum.KRYO.getCode();
    }
}
