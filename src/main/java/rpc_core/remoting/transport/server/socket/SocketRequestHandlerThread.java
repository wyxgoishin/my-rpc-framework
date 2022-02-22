package rpc_core.remoting.transport.server.socket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.codec.compressor.Compressor;
import rpc_core.remoting.handler.RequestHandler;
import rpc_core.codec.serializer.Serializer;

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
