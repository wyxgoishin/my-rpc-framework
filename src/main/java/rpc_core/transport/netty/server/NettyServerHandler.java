package rpc_core.transport.netty.server;

import io.netty.channel.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_core.provider.ServiceProvider;
import rpc_core.transport.RequestHandler;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler = new RequestHandler();
    private final ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        logger.info("服务器接收到请求：{}", rpcRequest);
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceProvider.getServiceProvider(interfaceName);
        Object result = requestHandler.handle(rpcRequest, service);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }
}
