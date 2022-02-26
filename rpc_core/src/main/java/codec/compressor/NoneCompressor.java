package codec.compressor;

import enumeration.CompressorEnum;

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
        return CompressorEnum.NONE.getCode();
    }
}
