package rpc_core.compresser;

import rpc_common.enumeration.CompressorCode;

public class NoneCompressor implements Compressor{
    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }

    @Override
    public int getCode() {
        return CompressorCode.NONE.getCode();
    }
}
