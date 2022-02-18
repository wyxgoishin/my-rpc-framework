package rpc_core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RpcResponseStatusBean {
    SUCCESS(200, "服务调用成功"),
    SERVICE_NOT_EXISTS(404, "请求服务不存在");


    private final Integer statusCode;
    private final String message;
}
