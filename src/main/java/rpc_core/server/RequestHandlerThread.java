package rpc_core.server;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.RpcRequest;
import rpc_core.RpcResponse;
import rpc_core.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
                RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
                String interfaceName = rpcRequest.getInterfaceName();
                Object service = serviceRegistry.getService(interfaceName);
                Object result = requestHandler.handle(rpcRequest, service);
                objectOutputStream.writeObject(RpcResponse.success(result));
                objectOutputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("发送请求或调用服务时发生错误：" + e);
        }
    }
}
