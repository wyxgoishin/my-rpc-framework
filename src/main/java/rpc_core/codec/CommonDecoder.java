package rpc_core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.PackageType;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.serializer.CommonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xDEADBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int magicNumber = in.readInt();
        if(magicNumber != MAGIC_NUMBER){
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_PROTOCOL.getErrorMessage(), magicNumber);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_PACKAGE_CODE.getErrorMessage(), packageCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PACKAGE_CODE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null){
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_SERIALIZER.getErrorMessage(), serializerCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_SERIALIZER);
        }

        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content);
        Object obj = serializer.deserialize(content, packageClass);
        out.add(obj);
    }

    public static Object readStreamAndDecode(InputStream in) throws IOException {
        byte[] numberBytes = new byte[4];
        in.read(numberBytes);
        int magicNumber = bytesToInt(numberBytes);
        if(magicNumber != MAGIC_NUMBER){
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_PROTOCOL.getErrorMessage(), magicNumber);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PROTOCOL);
        }

        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_PACKAGE_CODE.getErrorMessage(), packageCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PACKAGE_CODE);
        }

        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null){
            logger.error("{} ：{}", RpcExceptionBean.UNKNOWN_SERIALIZER.getErrorMessage(), serializerCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_SERIALIZER);
        }

        in.read(numberBytes);
        int length = bytesToInt(numberBytes);
        byte[] content = new byte[length];
        in.read(content);
        return serializer.deserialize(content, packageClass);
    }

    private static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }
}
