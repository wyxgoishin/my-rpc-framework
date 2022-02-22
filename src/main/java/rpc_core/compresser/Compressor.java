package rpc_core.compresser;

import rpc_common.enumeration.CompressorCode;

public interface Compressor {
    CompressorCode DEFAULT_COMPRESSOR = CompressorCode.NONE;

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);

    int getCode();

    static Compressor getByCode(int compressorCode){
        if(compressorCode == 0){
            return new NoneCompressor();
        }else if(compressorCode == 1){
            return new GzipCompressor();
        }else{
            return null;
        }
    }

    static Compressor getByCode(CompressorCode compressorCode){
        if(compressorCode != null){
            return getByCode(compressorCode.getCode());
        }else{
            return null;
        }
    }
}
