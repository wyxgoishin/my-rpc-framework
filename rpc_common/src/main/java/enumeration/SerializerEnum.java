package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerEnum {
    KRYO(0, "kryo"),
    JSON(1, "json"),
    PROTOBUF(2, "protobuf"),
    HESSIAN(3, "hessian");

    private final int code;
    private final String name;
}
