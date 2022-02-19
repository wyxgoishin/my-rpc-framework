package rpc_core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.exception.RpcException;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.registry.NacosServiceDiscovery;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.AbstractRpcClient;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient extends AbstractRpcClient {
    private static final Bootstrap bootstrap;
    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    static {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient(){
        this(DEFAULT_SERIALIZER_CODE.getCode());
    }

    public NettyClient(SerializerCode serializerCode){
        this(serializerCode.getCode());
    }

    public NettyClient(int serializerCode){
        serializer = CommonSerializer.getByCode(serializerCode);
        serviceDiscovery = new NacosServiceDiscovery();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new CommonDecoder())
                    .addLast(new CommonEncoder(serializer))
                    .addLast(new NettyClientHandler());
            }
        });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null){
            logger.error(RpcExceptionBean.SERIALIZER_NOT_EXISTS.getErrorMessage());
            throw new RpcException(RpcExceptionBean.SERIALIZER_NOT_EXISTS);
        }
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = getServiceChannel(inetSocketAddress);
            if(channel != null){
                logger.info("客户端连接到服务器 {}:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()){
                        logger.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                    }else{
                        logger.error("{} ：{}", RpcExceptionBean.SEND_MESSAGE_EXCEPTION, future1.cause());
                        throw new RpcException(RpcExceptionBean.SEND_MESSAGE_EXCEPTION);
                    }
                });
                channel.closeFuture().sync();
                // 由于这里的发送是非阻塞的，故发送后会立刻返回，而无法获得结果；因此通过 AttributeKey 阻塞获得结果并将其
                // 放入 ChannelHandlerContext中
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse;
            }
        } catch (InterruptedException e) {
            logger.error("{} ：{}", RpcExceptionBean.SEND_MESSAGE_EXCEPTION, e);
        }
        return RpcResponse.fail(RpcExceptionBean.SEND_MESSAGE_EXCEPTION);
    }

    private Channel getServiceChannel(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString() + serializer.getCode();
        if(channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            if(channel != null && channel.isActive()){
                return channel;
            }else{
                channelMap.remove(key);
            }
        }

        Channel channel;
        try {
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
                    logger.info("客户端连接成功");
                    completableFuture.complete(future.channel());
                }else{
                    logger.error(RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage());
                }
            });
            channel = completableFuture.get();
            channelMap.put(key, channel);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("{}: ", RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage(), e);
            return null;
        }
        return channel;
    }
}
