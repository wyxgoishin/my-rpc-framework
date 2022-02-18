package rpc_core.registry;

public interface ServiceRegistry {
    <K> void register(K service);
    Object getService(String serviceName);
}
