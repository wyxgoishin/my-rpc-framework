package rpc_core.transport.socket.server;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_core.codec.CommonDecoder;
import rpc_core.handler.RequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class SocketRequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
                RpcRequest rpcRequest = (RpcRequest) CommonDecoder.readStreamAndDecode(objectInputStream);
                Object result = requestHandler.handle(rpcRequest);
                objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            logger.error("{}：", RpcExceptionBean.SEND_MESSAGE_EXCEPTION.getErrorMessage(), e);
        }
    }
}
