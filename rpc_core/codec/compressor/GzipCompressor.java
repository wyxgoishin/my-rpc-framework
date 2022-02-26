package rpc_core.codec.compressor;

import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.CompressorEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class GzipCompressor implements Compressor {
    private static final int BUFFER_SIZE = 4 * 1024;

    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null){
            log.error("input byte array is null");
            throw new NullPointerException("input byte array is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)){
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("gzip compression failed", e);
            throw new RuntimeException("gzip compression failed");
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null){
            log.error("input byte array is null");
            throw new NullPointerException("input byte array is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPInputStream unzip = new GZIPInputStream(new ByteArrayInputStream(bytes))){
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while((n = unzip.read(buffer)) != -1){
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            log.error("gzip decompression failed", e);
            throw new RuntimeException("gzip decompression failed");
        }
    }

    @Override
    public int getCode() {
        return CompressorEnum.GZIP.getCode();
    }
}
