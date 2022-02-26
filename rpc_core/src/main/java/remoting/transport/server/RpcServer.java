package remoting.transport.server;

import remoting.transport.RpcEntity;

public interface RpcServer extends RpcEntity {
    void start();
    <T> void publishService(Object service, String serviceName);
}
