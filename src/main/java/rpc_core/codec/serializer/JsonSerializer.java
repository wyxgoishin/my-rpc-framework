package rpc_core.codec.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcRequest;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;

import java.io.IOException;

@Slf4j
public class JsonSerializer implements Serializer {
//    private static final Logger log = LoggerFactory.getLogger(JsonSerializer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("error occurred during serializing using json serializer");
            throw new RuntimeException("error occurred during serializing using json serializer");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            log.error("error occurred during deserializing using json serializer");
            throw new RuntimeException("error occurred during deserializing using json serializer");
        }
    }

    /*
    As Json serializer just change object to Json string and will lose the object type during serialization,
    thus we need to rejudge when deserialize Object array to ensure the array element type
     */
    private Object handleRequest(Object object) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) object;
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();
        for(int i = 0; i < parameterTypes.length; i++){
            Class<?> clazz = parameterTypes[i];
            if(!clazz.isAssignableFrom(parameters[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(parameters[i]);
                parameters[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerEnum.JSON.getCode();
    }
}
