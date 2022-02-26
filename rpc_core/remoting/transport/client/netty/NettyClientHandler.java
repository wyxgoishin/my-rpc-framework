package rpc_core.remoting.transport.client.netty;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.factory.SingletonFactory;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private final UnprocessedRequests unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        log.info(String.format("client received message: [%s]", rpcResponse));
        unprocessedRequests.complete(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("{}", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }
}
