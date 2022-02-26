package remoting.transport.client.netty;

import codec.CommonDecoder;
import codec.CommonEncoder;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.RpcExceptionBean;
import enumeration.SerializerEnum;
import exception.RpcException;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import registry.service_discovery.ServiceDiscovery;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.transport.AbstractRpcEntity;
import remoting.transport.client.AbstractRpcClient;

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
        super();
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

    public NettyClient(ServiceDiscovery serviceDiscovery){
        this(serviceDiscovery, AbstractRpcEntity.DEFAULT_SERIALIZER, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public NettyClient(ServiceDiscovery serviceDiscovery, SerializerEnum serializerEnum){
        this(serviceDiscovery, serializerEnum, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public NettyClient(ServiceDiscovery serviceDiscovery, SerializerEnum serializerEnum, CompressorEnum compressorEnum){
        if(serviceDiscovery == null){
            throw new RpcException(RpcExceptionBean.SERVICE_DISCOVERY_NOT_EXISTS);
        }
        serializer = Serializer.getByEnum(serializerEnum);
        compressor = Compressor.getByEnum(compressorEnum);
        this.serviceDiscovery = serviceDiscovery;
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
            log.error("serializer not set yet");
            throw new RuntimeException("serializer not set yet");
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getServiceName());
        Channel channel = getServiceChannel(inetSocketAddress);
        if(channel == null || !channel.isActive()){
            group.shutdownGracefully();
            return null;
        }
        log.info("trying to connect server[{}:{}]", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
            if(future1.isSuccess()){
                log.info(String.format("client sent message[%s] succeeded", rpcRequest));
            }else{
                future1.channel().close();
                resultFuture.completeExceptionally(future1.cause());
                log.error("{}", RpcExceptionBean.SEND_MESSAGE_EXCEPTION, future1.cause());
            }
        });
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
                    log.info("connect server succeeded");
                    completableFuture.complete(future.channel());
                }else{
                    log.error(RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage());
                }
            });
            channel = completableFuture.get();
            channelMap.put(key, channel);
        } catch (ExecutionException | InterruptedException e) {
            log.error("{}", RpcExceptionBean.CONNECTION_EXCEPTION.getErrorMessage(), e);
            return null;
        }
        return channel;
    }
}
