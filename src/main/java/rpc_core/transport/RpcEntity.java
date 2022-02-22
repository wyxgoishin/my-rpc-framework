package rpc_core.transport;

import rpc_common.enumeration.CompressorCode;
import rpc_common.enumeration.SerializerCode;
import rpc_core.compresser.Compressor;
import rpc_core.serializer.Serializer;

public interface RpcEntity {
    void setSerializer(SerializerCode serializerCode);
    void setSerializer(int serializerCode);
    void setSerializer(Serializer serializer);
    void serCompressor(CompressorCode compressorCode);
    void setCompressor(int compressorCode);
    void setCompressor(Compressor compressor);
}
