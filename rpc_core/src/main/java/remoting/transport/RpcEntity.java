package remoting.transport;

import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.SerializerEnum;

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
