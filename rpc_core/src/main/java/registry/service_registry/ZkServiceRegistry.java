package registry.service_registry;

import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import registry.AbstractRegistry;
import util.ZkUtil;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class ZkServiceRegistry extends AbstractRegistry implements ServiceRegistry{

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        CuratorFramework zkClient = ZkUtil.getZkClient(serverAddress);
        ZkUtil.registerService(zkClient, serviceName, inetSocketAddress);
    }
}
