package rpc_core.transport.socket.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.compresser.Compressor;
import rpc_core.handler.RequestHandler;
import rpc_core.serializer.Serializer;

import java.io.*;
import java.net.Socket;

@AllArgsConstructor
@Slf4j
public class SocketRequestHandlerThread implements Runnable {
//    private static final Logger log = LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private Serializer serializer;
    private Compressor compressor;

    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream()){
            try(OutputStream outputStream = socket.getOutputStream()){
                RpcRequest rpcRequest = (RpcRequest) CommonDecoder.readStreamAndDecode(inputStream);
                Object result = requestHandler.handle(rpcRequest);
                Object rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                CommonEncoder.encodeAndWriteToStream(outputStream, rpcResponse, serializer, compressor);
            }
        } catch (IOException e) {
            log.error("{}：", RpcExceptionBean.SEND_MESSAGE_EXCEPTION.getErrorMessage(), e);
        }
    }
}
