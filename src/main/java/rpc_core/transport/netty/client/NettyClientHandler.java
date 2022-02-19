package rpc_core.transport.netty.client;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        logger.info(String.format("客户端收到消息：%s", rpcResponse));
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        ctx.channel().attr(key).set(rpcResponse);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }
}
