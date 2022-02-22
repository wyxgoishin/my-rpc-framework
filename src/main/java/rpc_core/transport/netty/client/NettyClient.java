package rpc_core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.Bzip2Decoder;
import io.netty.handler.codec.compression.Bzip2Encoder;
import rpc_common.entity.RpcRequest;
import rpc_common.entity.RpcResponse;
import rpc_common.enumeration.CompressorCode;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_core.balancer.LoadBalancer;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.compresser.Compressor;
import rpc_core.discovery.NacosServiceDiscovery;
import rpc_core.serializer.Serializer;
import rpc_core.transport.AbstractRpcClient;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class NettyClient extends AbstractRpcClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private final UnprocessedRequests unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient(){
        this(DEFAULT_SERIALIZER_CODE.getCode(), DEFAULT_COMPRESSOR_CODE.getCode(), null);
    }

    public NettyClient(int serializerCode){
        this(serializerCode, DEFAULT_COMPRESSOR_CODE.getCode(), null);
    }

    public NettyClient(SerializerCode serializerCode){
        this(serializerCode.getCode(), DEFAULT_SERIALIZER_CODE.getCode(), null);
    }

    public NettyClient(SerializerCode serializerCode, CompressorCode compressorCode){
        this(serializerCode.getCode(), compressorCode.getCode(), null);
    }

    public NettyClient(int serializerCode, int compressorCode){
        this(serializerCode, compressorCode, null);
    }

    public NettyClient(SerializerCode serializerCode, CompressorCode compressorCode, LoadBalancer loadBalancer){
        this(serializerCode.getCode(), compressorCode.getCode(), loadBalancer);
    }

    public NettyClient(int serializerCode, int compressorCode, LoadBalancer loadBalancer){
        serializer = Serializer.getByCode(serializerCode);
        compressor = Compressor.getByCode(compressorCode);
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast(new CommonEncoder(serializer, compressor))
                        .addLast(new NettyClientHandler());
            }
        });
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if(serializer == null){
            log.error(RpcExceptionBean.SERIALIZER_NOT_EXISTS.getErrorMessage());
            throw new RpcException(RpcExceptionBean.SERIALIZER_NOT_EXISTS);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getServiceName());
        Channel channel = getServiceChannel(inetSocketAddress);
        if(channel == null || !channel.isActive()){
            group.shutdownGracefully();
            return null;
        }
        log.info("客户端连接到服务器 {}:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
            if(future1.isSuccess()){
                log.info(String.format("客户端发送消息：%s", rpcRequest));
            }else{
                future1.channel().close();
                resultFuture.completeExceptionally(future1.cause());
                log.error("{}：", RpcExceptionBean.SEND_MESSAGE_EXCEPTION, future1.cause());
            }
        });
        /* 先前的阻塞版
        由于这里的发送是非阻塞的，故发送后会立刻返回，而无法获得结果；
        因此通过 AttributeKey 阻塞获得结果并将其放入 ChannelHandlerContext中

        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        RpcResponse rpcResponse = channel.attr(key).get();
        return rpcResponse;

         */
        return resultFuture;
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
                    log.info("客户端连接成功");
                    completableFuture.complete(future.channel());
                }else{
                    log.error(RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage());
                }
            });
            channel = completableFuture.get();
            channelMap.put(key, channel);
        } catch (ExecutionException | InterruptedException e) {
            log.error("{}: ", RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage(), e);
            return null;
        }
        return channel;
    }
}
