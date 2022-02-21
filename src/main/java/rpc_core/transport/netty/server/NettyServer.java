package rpc_core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.exception.RpcException;
import rpc_core.codec.CommonDecoder;
import rpc_core.codec.CommonEncoder;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.registry.NacosServiceRegistry;
import rpc_core.hook.ShutdownHook;
import rpc_core.serializer.CommonSerializer;
import rpc_core.transport.AbstractRpcServer;

public class NettyServer extends AbstractRpcServer {
    public NettyServer(String host, int port){
        this(host, port, DEFAULT_SERIALIZER_CODE.getCode());
    }

    public NettyServer(String host, int port, SerializerCode serializerCode){
        this(host, port, serializerCode.getCode());
    }

    public NettyServer(String host, int port, int code){
        this.host = host;
        this.port = port;
        serializer = CommonSerializer.getByCode(code);
        serviceRegistry = new NacosServiceRegistry();
//        serviceProvider = new DefaultServiceProvider();
        scanService();
    }


    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 对应TCP/IP协议的listen函数中的backlog函数，用来初始化服务端可连接队列
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 禁用 Nagle 算法，表示允许小包发送（TPC/IP协议中默认开启Nagle算法，它通过减少需要传输的数据包来优化网络）
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonDecoder())
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            logger.info("{} ：", RpcExceptionBean.BOOT_SERVER_FAILED.getErrorMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        if(serializer == null){
            logger.error(RpcExceptionBean.SERIALIZER_NOT_EXISTS.getErrorMessage());
            throw new RpcException(RpcExceptionBean.SERIALIZER_NOT_EXISTS);
        }
        super.publishService(service, serviceName);
    }
}
