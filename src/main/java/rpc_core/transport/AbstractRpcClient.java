package rpc_core.transport;

import rpc_common.entity.RpcRequest;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.balancer.LoadBalancer;
import rpc_core.discovery.ServiceDiscovery;

public class AbstractRpcClient extends AbstractRpcEntity implements RpcClient{
    protected ServiceDiscovery serviceDiscovery;

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        throw new RpcException(RpcExceptionBean.SEND_MESSAGE_EXCEPTION);
    }
}
