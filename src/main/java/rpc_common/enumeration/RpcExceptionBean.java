package rpc_common.enumeration;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum RpcExceptionBean {
    SERVICE_NOT_IMPLEMENTS_ANY_INTERFACES("所请求服务未实现任何接口"),
    SERVICE_NOT_FOUND("未找到所请求的服务接口"),
    METHOD_NOT_FOUND("未在所请求的服务接口中找到请求的方法"),
    RUNTIME_ERROR("请求服务在运行期发生错误"),
    CLIENT_UNAVAILABLE("客户端功能不可用"),
    UNKNOWN_PROTOCOL("无法识别的协议包"),
    UNKNOWN_PACKAGE_CODE("无法识别的数据包类型"),
    UNKNOWN_SERIALIZER("无法识别的序列化器类型");

    private final String errorMessage;
}
