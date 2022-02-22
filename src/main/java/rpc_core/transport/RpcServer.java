package rpc_core.transport;

public interface RpcServer extends RpcEntity {
    void start();
    <T> void publishService(Object service, String serviceName);
}
