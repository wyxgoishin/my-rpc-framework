package rpc_core.remoting.transport.server;

import rpc_core.remoting.transport.RpcEntity;

public interface RpcServer extends RpcEntity {
    void start();
    <T> void publishService(Object service, String serviceName);
}
