package rpc_core.registry.service_registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import rpc_common.util.ZkUtil;
import rpc_core.registry.AbstractRegistry;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class ZkServiceRegistry extends AbstractRegistry implements ServiceRegistry{

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        CuratorFramework zkClient = ZkUtil.getZkClient(serverAddress);
        ZkUtil.registerService(zkClient, serviceName, inetSocketAddress);
    }
}
