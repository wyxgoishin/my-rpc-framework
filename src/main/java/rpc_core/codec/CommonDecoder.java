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

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xDEADBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int magicNumber = in.readInt();
        if(magicNumber != MAGIC_NUMBER){
            logger.error("无法识别的协议包：{}", magicNumber);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("无法识别的数据包类型：{}", packageCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_PACKAGE_CODE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null){
            logger.error("无法识别的序列化器类型：{}", serializerCode);
            throw new RpcException(RpcExceptionBean.UNKNOWN_SERIALIZER);
        }

        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content);
        Object obj = serializer.deserialize(content, packageClass);
        out.add(obj);
    }
}
