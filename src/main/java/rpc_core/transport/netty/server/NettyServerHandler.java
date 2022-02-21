package rpc_core.transport.netty.server;

import io.netty.channel.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.factory.SingletonFactory;
import rpc_core.provider.ServiceProvider;
import rpc_core.handler.RequestHandler;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        logger.info("服务器接收到请求：{}", rpcRequest);
        Object result = requestHandler.handle(rpcRequest);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }
}
