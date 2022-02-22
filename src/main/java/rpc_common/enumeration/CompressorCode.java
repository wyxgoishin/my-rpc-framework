package rpc_common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressorCode {
    NONE(0),
    GZIP(1);

    private final int code;
}
