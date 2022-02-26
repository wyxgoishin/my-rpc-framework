package remoting.transport.server.socket;

import codec.CommonDecoder;
import codec.CommonEncoder;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.RpcExceptionBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.handler.RequestHandler;

import java.io.*;
import java.net.Socket;

@AllArgsConstructor
@Slf4j
public class SocketRequestHandlerThread implements Runnable {
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
