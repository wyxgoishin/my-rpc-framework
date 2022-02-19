package rpc_core.transport;

import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.provider.ServiceProvider;
import rpc_core.registry.ServiceRegistry;

import java.net.InetSocketAddress;

public class AbstractRpcServer extends AbstractRpcEntity implements RpcServer {
    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
