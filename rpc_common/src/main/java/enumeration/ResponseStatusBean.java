package enumeration;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum ResponseStatusBean {
    SUCCESS(200, "service call succeeded"),
    FAIL(400, "service call failed"),
    ;

    private final Integer statusCode;
    private final String message;
}
