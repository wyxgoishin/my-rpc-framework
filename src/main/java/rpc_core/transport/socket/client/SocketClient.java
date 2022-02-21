package rpc_core.transport.socket.client;

import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_core.balancer.LoadBalancer;
import rpc_core.codec.CommonEncoder;
import rpc_core.discovery.NacosServiceDiscovery;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.AbstractRpcClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient extends AbstractRpcClient {
    public SocketClient(){
        this(DEFAULT_SERIALIZER_CODE, null);
    }

    public SocketClient(LoadBalancer loadBalancer){
       this(DEFAULT_SERIALIZER_CODE, loadBalancer);
    }

    public SocketClient(SerializerCode serializerCode){
       this(serializerCode, null);
    }

    public SocketClient(SerializerCode serializerCode, LoadBalancer loadBalancer){
       this(serializerCode.getCode(), loadBalancer);
    }

    public SocketClient(int code, LoadBalancer loadBalancer){
        this.serializer = CommonSerializer.getByCode(code);
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
    }

    public Object sendRequest(RpcRequest rpcRequest){
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getServiceName());
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            CommonEncoder.encodeAndWriteToStream(objectOutputStream, rpcRequest, serializer);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            logger.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION, e);
            return RpcResponse.fail(RpcExceptionBean.PROCESS_SERVICE_EXCEPTION, rpcRequest.getRequestId());
        }
    }
}
