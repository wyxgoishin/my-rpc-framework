package rpc_core.registry.service_registry;

import rpc_core.registry.Registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry extends Registry {
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
}
