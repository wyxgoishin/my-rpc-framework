package registry.service_discovery;

import registry.Registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery extends Registry {
    InetSocketAddress lookupService(String serviceName);
}
