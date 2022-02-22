package rpc_core.transport.netty.server;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.factory.SingletonFactory;
import rpc_core.handler.RequestHandler;

@AllArgsConstructor
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
//    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        if(rpcRequest.isHeartBeat()){
            log.info("服务器收到客户端的心跳包...");
            return;
        }
        log.info("服务器接收到请求：{}", rpcRequest);
        Object result = requestHandler.handle(rpcRequest);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("{} : ", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                log.info("长时间未收到客户端的心跳包，断开连接...");
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
