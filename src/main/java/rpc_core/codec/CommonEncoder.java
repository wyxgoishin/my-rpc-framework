package rpc_core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import rpc_common.entity.RpcRequest;
import rpc_common.enumeration.PackageType;
import rpc_core.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xDEADBABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] content = serializer.serialize(msg);
        out.writeInt(content.length);
        out.writeBytes(content);
    }

    public static void encodeAndWriteToStream(OutputStream out, Object msg, CommonSerializer serializer) throws IOException {
        out.write(intToBytes(MAGIC_NUMBER));
        if(msg instanceof RpcRequest){
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        }else{
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        out.write(intToBytes(serializer.getCode()));
        byte[] content = serializer.serialize(msg);
        out.write(intToBytes(content.length));
        out.write(content);
        out.flush();
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
