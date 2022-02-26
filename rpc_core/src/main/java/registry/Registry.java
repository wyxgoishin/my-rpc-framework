package registry;

import registry.service_discovery.NacosServiceDiscovery;
import registry.service_discovery.ZkServiceDiscovery;
import registry.service_registry.NacosServiceRegistry;
import registry.service_registry.ZkServiceRegistry;

public interface Registry {
    static AbstractRegistry getRegistryByNameAndType(String name, String type) {
        if(name == null || type == null){
            throw new RuntimeException("empty registry");
        }else{
            if(name.equals("nacos")){
                if(type.equals("registry")){
                    return new NacosServiceRegistry();
                }else if(type.equals("discovery")){
                    return new NacosServiceDiscovery();
                }else{
                    throw new RuntimeException(String.format("unsupported registry type: %s-%s", name, type));
                }
            }else if(name.equals("zookeeper")){
                if(type.equals("registry")){
                    return new ZkServiceRegistry();
                }else if(type.equals("discovery")){
                    return new ZkServiceDiscovery();
                }else{
                    throw new RuntimeException(String.format("unsupported registry type: %s-%s", name, type));
                }
            }else{
                throw new RuntimeException(String.format("unsupported registry type: %s-%s", name, type));
            }
        }
    }
}
