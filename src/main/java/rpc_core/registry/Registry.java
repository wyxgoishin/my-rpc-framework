package rpc_core.registry;

import lombok.extern.slf4j.Slf4j;
import rpc_common.enumeration.RegistryEnum;
import rpc_core.registry.service_discovery.NacosServiceDiscovery;
import rpc_core.registry.service_discovery.ZkServiceDiscovery;
import rpc_core.registry.service_registry.NacosServiceRegistry;
import rpc_core.registry.service_registry.ZkServiceRegistry;

import java.util.Objects;

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
