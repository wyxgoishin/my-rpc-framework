package rpc_core.remoting.transport.client;

import rpc_core.remoting.dto.RpcRequest;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_core.registry.service_discovery.ServiceDiscovery;
import rpc_core.remoting.transport.AbstractRpcEntity;

public class AbstractRpcClient extends AbstractRpcEntity implements RpcClient{
    protected ServiceDiscovery serviceDiscovery;

    public AbstractRpcClient(){
        super();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        throw new RpcException(RpcExceptionBean.SEND_MESSAGE_EXCEPTION);
    }
}
