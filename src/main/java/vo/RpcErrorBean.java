package vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum RpcErrorBean {
    SERVICE_NOT_IMPLEMENTS_ANY_INTERFACES("所请求服务未实现任何接口"),
    SERVICE_NOT_FOUND("未找到所请求的服务接口"),
    METHOD_NOT_FOUND("未在所请求的服务接口中找到请求的方法"),
    RUNTIME_ERROR("请求服务在运行期发生错误");

    private final String errorMessage;
}
