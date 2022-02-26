package rpc_core.remoting.transport.client.socket;

import rpc_common.exception.RpcException;
import rpc_core.registry.service_discovery.ServiceDiscovery;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;
import rpc_core.balancer.LoadBalancer;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.codec.compressor.Compressor;
import rpc_core.registry.service_discovery.NacosServiceDiscovery;
import rpc_core.codec.serializer.Serializer;
import rpc_core.remoting.transport.client.AbstractRpcClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient extends AbstractRpcClient {
    public SocketClient(){
        super();
    }

    public SocketClient(ServiceDiscovery serviceDiscovery){
       this(serviceDiscovery, DEFAULT_SERIALIZER, DEFAULT_COMPRESSOR);
    }

    public SocketClient(ServiceDiscovery serviceDiscovery, SerializerEnum serializerEnum){
        this(serviceDiscovery, serializerEnum, DEFAULT_COMPRESSOR);
    }

    public SocketClient(ServiceDiscovery serviceDiscovery, SerializerEnum serializerEnum, CompressorEnum compressorEnum){
        if(serviceDiscovery == null){
            throw new RpcException(RpcExceptionBean.SERVICE_DISCOVERY_NOT_EXISTS);
        }
        this.serializer = Serializer.getByEnum(serializerEnum);
        this.compressor = Compressor.getByEnum(compressorEnum);
        this.serviceDiscovery = serviceDiscovery;
    }

    public Object sendRequest(RpcRequest rpcRequest){
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getServiceName());
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            CommonEncoder.encodeAndWriteToStream(outputStream, rpcRequest, serializer, compressor);
            return CommonDecoder.readStreamAndDecode(inputStream);
        } catch (IOException e){
            log.error("{}", RpcExceptionBean.GET_MESSAGE_EXCEPTION.getErrorMessage(), e);
            return RpcResponse.fail(RpcExceptionBean.GET_MESSAGE_EXCEPTION, rpcRequest.getRequestId());
        }
    }
}
