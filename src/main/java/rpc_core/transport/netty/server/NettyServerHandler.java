package rpc_core.transport.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_core.registry.DefaultServiceRegistry;
import rpc_core.registry.ServiceRegistry;
import rpc_core.transport.RequestHandler;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        logger.info("服务器接收到请求：{}", rpcRequest);
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        Object result = requestHandler.handle(rpcRequest, service);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("处理过程中发生错误：", cause);
        ctx.close();
    }
}
