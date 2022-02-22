package rpc_common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressorEnum {
    NONE(0, "none"),
    GZIP(1, "gzip");

    private final int code;
    private final String name;
}
