package rpc_core.registry;

import rpc_common.enumeration.RegistryEnum;
import rpc_core.registry.service_discovery.NacosServiceDiscovery;
import rpc_core.registry.service_discovery.ZkServiceDiscovery;
import rpc_core.registry.service_registry.NacosServiceRegistry;
import rpc_core.registry.service_registry.ZkServiceRegistry;

import java.util.Objects;

public interface Registry {
    static AbstractRegistry getRegistryByNameAndType(String name, String type) {
        if(name == null || type == null){
            throw new RuntimeException("unsupported registry!");
        }else{
            if(name.equals("nacos")){
                if(type.equals("registry")){
                    return new NacosServiceRegistry();
                }else if(type.equals("discovery")){
                    return new NacosServiceDiscovery();
                }else{
                    throw new RuntimeException("unsupported registry");
                }
            }else if(name.equals("zookeeper")){
                if(type.equals("registry")){
                    return new ZkServiceRegistry();
                }else if(type.equals("discovery")){
                    return new ZkServiceDiscovery();
                }else{
                    throw new RuntimeException("unsupported registry");
                }
            }else{
                throw new RuntimeException("unsupported registry");
            }
        }
    }
}
