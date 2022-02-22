package rpc_core.remoting.transport.server.netty;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rpc_core.remoting.dto.RpcRequest;
import rpc_core.remoting.dto.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.factory.SingletonFactory;
import rpc_core.remoting.handler.RequestHandler;

@AllArgsConstructor
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
//    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler = SingletonFactory.getInstance(RequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        if(rpcRequest.isHeartBeat()){
            log.info("server get heart beat pack from client...");
            return;
        }
        log.info("server get request[{}]", rpcRequest);
        Object result = requestHandler.handle(rpcRequest);
        ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("{}", RpcExceptionBean.PROCESS_SERVICE_EXCEPTION.getErrorMessage(), cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                log.info("not received heart beat pack from client for a long time and will close the connection...");
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
