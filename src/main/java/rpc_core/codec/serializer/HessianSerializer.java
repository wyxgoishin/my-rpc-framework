package rpc_core.codec.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;
import rpc_common.exception.RpcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error(RpcExceptionBean.SERIALIZAION_ERROR.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.SERIALIZAION_ERROR);
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    log.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            log.error(RpcExceptionBean.SERIALIZAION_ERROR.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.SERIALIZAION_ERROR);
        } finally {
            if (hessianInput != null) {
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerEnum.HESSIAN.getCode();
    }
}
