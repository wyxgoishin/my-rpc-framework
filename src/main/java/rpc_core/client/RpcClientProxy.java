package rpc_core.client;

import lombok.AllArgsConstructor;
import rpc_core.RpcRequest;
import rpc_core.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public <K> K getProxy(Class<K> clazz){
        return (K) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                    .interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameters(args)
                    .build();
        RpcClient rpcClient = new RpcClient();
        RpcResponse rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest, host, port);
        return rpcResponse.getData();
    }
}
