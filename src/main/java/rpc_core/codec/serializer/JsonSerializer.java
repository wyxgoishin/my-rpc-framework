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
            log.error("{} ：", RpcExceptionBean.SERIALIZAION_ERROR.getErrorMessage(), e);
            return null;
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
            log.error("{} ：", RpcExceptionBean.SERIALIZAION_ERROR.getErrorMessage(), e);
        }
        return null;
    }

    /*
        这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
        (可能会反序列化失败，因为序列化器会根据字段类型进行反序列化）需要重新判断处理
        上面提到的这种情况不会在其他序列化方式中出现，因为其他序列化方式是转换成字节数组，
        会记录对象的信息，而 JSON 方式本质上只是转换成 JSON 字符串，会丢失对象的类型信息。
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
