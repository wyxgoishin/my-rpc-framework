package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum RpcExceptionBean {
    // transport
    CONNECTION_EXCEPTION("exception occurred when trying to make connection"),
    SEND_MESSAGE_EXCEPTION("exception occurred when trying to send message"),
    GET_MESSAGE_EXCEPTION("exception occurred when trying to get message"),
    PROCESS_SERVICE_EXCEPTION("exception occurred when processing the service"),
    // service
    SERVICE_NOT_FOUND("service not found"),
    METHOD_NOT_FOUND("method not found in provided service interface"),
    SERVICE_RUNTIME_EXCEPTION("exception occurred when running the service"),
    UNKNOWN_SERVICE_REGISTRY_OR_DISCOVERY("unknown service registry or discovery type"),
    // codec
    UNKNOWN_PROTOCOL("unknown protocol pack type"),
    UNKNOWN_PACKAGE_CODE("unknown data pack type"),
    UNKNOWN_SERIALIZER("unknown serializer type"),
    UNKNOWN_COMPRESSOR("unknown compression type"),
    // others
    BOOT_SERVER_FAILED("boot server failed"),
    SERVICE_REGISTRY_NOT_EXISTS("service registry not exists in current server"),
    SERVICE_DISCOVERY_NOT_EXISTS("service discovery not exists in current server"),
    ;

    private final String errorMessage;
}
