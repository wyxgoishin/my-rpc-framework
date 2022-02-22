package rpc_core.remoting.transport;

import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.SerializerEnum;
import rpc_core.codec.compressor.Compressor;
import rpc_core.codec.serializer.Serializer;

public interface RpcEntity {
    void setSerializer(SerializerEnum serializerEnum);
    void setSerializer(int serializerCode);
    void setSerializer(String serializerName);
    void setSerializer(Serializer serializer);
    void serCompressor(CompressorEnum compressorEnum);
    void setCompressor(int compressorCode);
    void setCompressor(String compressorName);
    void setCompressor(Compressor compressor);
}
