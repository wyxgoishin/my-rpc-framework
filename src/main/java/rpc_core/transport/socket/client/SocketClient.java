package rpc_core.transport.socket.client;

import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.CompressorCode;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_core.balancer.LoadBalancer;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.compresser.Compressor;
import rpc_core.discovery.NacosServiceDiscovery;
import rpc_core.serializer.Serializer;
import rpc_core.transport.AbstractRpcClient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient extends AbstractRpcClient {
    public SocketClient(){
        this(DEFAULT_SERIALIZER_CODE, DEFAULT_COMPRESSOR_CODE);
    }

    public SocketClient(SerializerCode serializerCode){
       this(serializerCode, DEFAULT_COMPRESSOR_CODE, null);
    }

    public SocketClient(int serializerCode){
        this(serializerCode, DEFAULT_COMPRESSOR_CODE.getCode(), null);
    }

    public SocketClient(SerializerCode serializerCode, CompressorCode compressorCode){
       this(serializerCode.getCode(), compressorCode.getCode(), null);
    }

    public SocketClient(int serializerCode, int compressorCode){
        this(serializerCode, compressorCode, null);
    }

    public SocketClient(SerializerCode serializerCode, CompressorCode compressorCode, LoadBalancer loadBalancer){
        this(serializerCode.getCode(), compressorCode.getCode(), loadBalancer);
    }

    public SocketClient(int serializerCode, int compressorCode, LoadBalancer loadBalancer){
        this.serializer = Serializer.getByCode(serializerCode);
        this.compressor = Compressor.getByCode(compressorCode);
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
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
            log.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION, e);
            return RpcResponse.fail(RpcExceptionBean.PROCESS_SERVICE_EXCEPTION, rpcRequest.getRequestId());
        }
    }
}
