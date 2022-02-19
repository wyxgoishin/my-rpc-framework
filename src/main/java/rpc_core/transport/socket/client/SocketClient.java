package rpc_core.transport.socket.client;

import lombok.AllArgsConstructor;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_core.transport.AbstractRpcClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class SocketClient extends AbstractRpcClient {
    private String host;
    private int port;


    public Object sendRequest(RpcRequest rpcRequest){
        try(Socket socket = new Socket(host, port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            logger.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION, e);
            return RpcResponse.fail(RpcExceptionBean.PROCESS_SERVICE_EXCEPTION);
        }
    }
}
