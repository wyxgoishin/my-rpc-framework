package registry.service_registry;

import registry.Registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry extends Registry {
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
}
