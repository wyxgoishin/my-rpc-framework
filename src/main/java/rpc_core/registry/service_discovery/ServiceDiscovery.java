package rpc_core.registry.service_discovery;

import rpc_core.registry.Registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery extends Registry {
    InetSocketAddress lookupService(String serviceName);
}
