package vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum ResponseStatusBean {
    SUCCESS(200, "服务调用成功"),
    FAIL(400, "服务调用失败"),
    ;

    private final Integer statusCode;
    private final String message;
}
