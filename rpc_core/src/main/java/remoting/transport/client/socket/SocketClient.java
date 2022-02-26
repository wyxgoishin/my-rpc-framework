package remoting.transport.client.socket;

import codec.CommonDecoder;
import codec.CommonEncoder;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.RpcExceptionBean;
import enumeration.SerializerEnum;
import exception.RpcException;
import registry.service_discovery.ServiceDiscovery;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.transport.AbstractRpcEntity;
import remoting.transport.client.AbstractRpcClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient extends AbstractRpcClient {
    public SocketClient(){
        super();
    }

    public SocketClient(ServiceDiscovery serviceDiscovery){
       this(serviceDiscovery, AbstractRpcEntity.DEFAULT_SERIALIZER, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public SocketClient(ServiceDiscovery serviceDiscovery, SerializerEnum serializerEnum){
        this(serviceDiscovery, serializerEnum, AbstractRpcEntity.DEFAULT_COMPRESSOR);
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
