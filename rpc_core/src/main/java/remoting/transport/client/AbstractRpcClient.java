package remoting.transport.client;
import enumeration.RpcExceptionBean;
import exception.RpcException;
import registry.service_discovery.ServiceDiscovery;
import remoting.dto.RpcRequest;
import remoting.transport.AbstractRpcEntity;

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
