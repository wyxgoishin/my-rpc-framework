package rpc_core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import rpc_common.entity.RpcRequest;
import rpc_common.enumeration.PackageType;
import rpc_core.compresser.Compressor;
import rpc_core.serializer.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xDEADBABE;
    private final Serializer serializer;
    private final Compressor compressor;

    public CommonEncoder(Serializer serializer){
        this(serializer, null);
    }

    public CommonEncoder(Compressor compressor){
        this(null, compressor);
    }

    public CommonEncoder(Serializer serializer, Compressor compressor){
        this.serializer = serializer;
        this.compressor = compressor;
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
        out.writeInt(compressor.getCode());
        byte[] content = serializer.serialize(msg);
        byte[] compressedContent = compressor.compress(content);
        out.writeInt(compressedContent.length);
        out.writeBytes(compressedContent);
    }

    public static void encodeAndWriteToStream(OutputStream out, Object msg, Serializer serializer, Compressor compressor) throws IOException {
        out.write(intToBytes(MAGIC_NUMBER));
        if(msg instanceof RpcRequest){
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        }else{
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        out.write(intToBytes(serializer.getCode()));
        out.write(intToBytes(compressor.getCode()));
        byte[] content = serializer.serialize(msg);
        byte[] compressedContent = compressor.compress(content);
        out.write(intToBytes(compressedContent.length));
        out.write(compressedContent);
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
