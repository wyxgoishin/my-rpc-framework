package remoting.transport.server.netty;

import annotation.ServiceScan;
import codec.CommonDecoder;
import codec.CommonEncoder;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.RpcExceptionBean;
import enumeration.SerializerEnum;
import exception.RpcException;
import hook.ShutdownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import registry.service_registry.ServiceRegistry;
import remoting.transport.AbstractRpcEntity;
import remoting.transport.server.AbstractRpcServer;
import util.ReflectUtil;

import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {
    public NettyServer(){
        super();
    }

    public NettyServer(String host, int port, ServiceRegistry serviceRegistry){
        this(host, port, serviceRegistry, AbstractRpcEntity.DEFAULT_SERIALIZER, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public NettyServer(String host, int port, ServiceRegistry serviceRegistry, SerializerEnum serializerEnum){
        this(host, port, serviceRegistry, serializerEnum, AbstractRpcEntity.DEFAULT_COMPRESSOR);
    }

    public NettyServer(String host, int port, ServiceRegistry serviceRegistry, SerializerEnum serializerEnum, CompressorEnum compressorEnum){
        if(serviceRegistry == null){
            throw new RpcException(RpcExceptionBean.SERVICE_REGISTRY_NOT_EXISTS);
        }
        this.host = host;
        this.port = port;
        serializer = Serializer.getByEnum(serializerEnum);
        compressor = Compressor.getByEnum(compressorEnum);
        this.serviceRegistry = serviceRegistry;
        Class<?> bootClass = ReflectUtil.getBootClassByStackTrace();
        if(bootClass.isAnnotationPresent(ServiceScan.class)){
            scanService(bootClass);
        }
    }


    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook(registryUtilClass);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // backlog() function in tcp/ip protocol, used to initialize connectable queue in server
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // deactivate Nagle algorithm and allow sending small pack(as our request pack is not so big)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonDecoder())
                                    .addLast(new CommonEncoder(serializer, compressor))
                                    .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e){
            log.info("{}", RpcExceptionBean.BOOT_SERVER_FAILED.getErrorMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        if(serializer == null){
            log.error("serializer not set yet");
            throw new RuntimeException("serializer not set yet");
        }
        super.publishService(service, serviceName);
    }
}
