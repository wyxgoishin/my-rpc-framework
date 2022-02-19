package rpc_core;

import lombok.AllArgsConstructor;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_core.transport.socket.client.SocketClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {
    private RpcClient rpcClient;

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
        RpcResponse rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest);
        return rpcResponse.getData();
    }
}
