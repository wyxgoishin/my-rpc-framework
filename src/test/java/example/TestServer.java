package example;

import rpc_core.annotation.ServiceScan;
import rpc_core.transport.RpcServer;
import rpc_core.transport.netty.server.NettyServer;

@ServiceScan("example.service.impl")
public class TestServer {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9000;
        // RpcServer server = new SocketServer(host, port);
        RpcServer server = new NettyServer(host, port);
        server.start();
    }
}
