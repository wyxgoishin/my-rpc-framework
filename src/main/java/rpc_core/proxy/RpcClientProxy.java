package rpc_core.proxy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.util.RpcResponseChecker;
import rpc_core.remoting.transport.client.RpcClient;
import rpc_core.remoting.transport.client.netty.NettyClient;
import rpc_core.remoting.transport.client.socket.SocketClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Data
@Slf4j
public class RpcClientProxy implements InvocationHandler {
//    private static final Logger log = LoggerFactory.getLogger(RpcClientProxy.class);
    private RpcClient rpcClient;
    private String serviceName;

    public <K> K getProxy(Class<K> clazz){
        return (K) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    public RpcClientProxy(RpcClient rpcClient){
        this(rpcClient, null);
    }

    public RpcClientProxy(RpcClient rpcClient, String serviceName){
        this.rpcClient = rpcClient;
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        if(serviceName != null){
            rpcRequest.setServiceName(serviceName);
        }else{
            rpcRequest.setServiceName(method.getDeclaringClass().getName());
        }
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        RpcResponse rpcResponse = null;
        if(rpcClient instanceof SocketClient){
            rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest);
        }else if(rpcClient instanceof NettyClient){
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) rpcClient.sendRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                log.error(RpcExceptionBean.SERVICE_RUNTIME_EXCEPTION.getErrorMessage());
                rpcResponse = RpcResponse.fail(RpcExceptionBean.SERVICE_RUNTIME_EXCEPTION, rpcRequest.getRequestId());
            }
        }
        RpcResponseChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
