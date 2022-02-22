package rpc_common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum RpcExceptionBean {
    // 服务器和客户端相关
    CONNECTION_EXCEPTION("建立连接时发生错误;"),
    BOOT_SERVER_FAILED("启动服务器失败;"),
    SEND_MESSAGE_EXCEPTION("发送消息时发生错误;"),
    PROCESS_SERVICE_EXCEPTION("处理服务时发生错误;"),
    // 服务相关
    SERVICE_NOT_FOUND("未找到所请求的服务接口: "),
    METHOD_NOT_FOUND("未在所请求的服务接口中找到请求的方法;"),
    SERVICE_RUNTIME_EXCEPTION("请求服务在运行期发生错误;"),
    RESPONSE_NOT_MATCH("请求与响应信息 ID 不匹配"),
    // 序列化/反序列化相关
    SERIALIZER_NOT_EXISTS("未设置序列化/反序列化器;"),
    UNKNOWN_PROTOCOL("无法识别的协议包"),
    UNKNOWN_PACKAGE_CODE("无法识别的数据包类型"),
    UNKNOWN_SERIALIZER("无法识别的序列化器类型"),
    SERIALIZAION_ERROR("序列化或反序列化过程中发生错误;"),
    // Nacos 相关
    CONNECT_NACOS_FAILED("连接到 Nacos 时发生错误;"),
    REGISTER_SERVICE_TO_NACOS_FAILED("向 Nacos 注册服务时发生错误;"),
    LOOKUP_SERVICE_IN_NACOS_FAILED("在 Nacos 中查找对应服务时发生错误;"),
    DEREGISTER_NACOS_INSTANCE_FAILED("服务注销失败;"),
    // 加载服务相关
    LOAD_BOOT_CLASS_FAILED("启动类加载错误;"),
    // 其他
    UNKNOWN_SERVICE_REGISTRTY("未知的服务注册中心"),
    UNKNOWN_COMPRESSOR("未知的压缩类型");

    private final String errorMessage;
}
