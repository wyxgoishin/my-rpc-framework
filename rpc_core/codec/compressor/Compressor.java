package rpc_core.codec.compressor;

import rpc_common.enumeration.CompressorEnum;

public interface Compressor {
    CompressorEnum DEFAULT_COMPRESSOR = CompressorEnum.NONE;

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);

    int getCode();

    static Compressor getByCode(int code){
        if(code == CompressorEnum.NONE.getCode()){
            return new NoneCompressor();
        }else if(code == CompressorEnum.GZIP.getCode()){
            return new GzipCompressor();
        }else{
            return new NoneCompressor();
        }
    }

    static Compressor getByName(String name){
        if(name == null){
            return new NoneCompressor();
        }else{
            if(name.equals(CompressorEnum.NONE.getName())){
                return new NoneCompressor();
            }else if(name.equals(CompressorEnum.GZIP.getName())){
                return new GzipCompressor();
            }else{
                return new NoneCompressor();
            }
        }
    }


    static Compressor getByEnum(CompressorEnum compressorEnum){
        return compressorEnum == null ? new NoneCompressor() : getByName(compressorEnum.getName());
    }
}
