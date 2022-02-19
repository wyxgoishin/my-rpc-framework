package rpc_core.transport;

import lombok.AllArgsConstructor;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;

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
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
//        RpcRequest rpcRequest = RpcRequest.builder()
//                    .interfaceName(method.getDeclaringClass().getName())
//                    .methodName(method.getName())
//                    .parameterTypes(method.getParameterTypes())
//                    .parameters(args)
//                    .build();
        RpcResponse rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest);
        return rpcResponse.getData();
    }
}
