package rpc_core.remoting.transport.server;

import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_common.util.ReflectUtil;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.provider.ServiceProvider;
import rpc_core.registry.service_registry.ServiceRegistry;
import rpc_core.annotation.Service;
import rpc_core.annotation.ServiceScan;
import rpc_core.remoting.transport.AbstractRpcEntity;

import java.net.InetSocketAddress;
import java.util.*;

public abstract class AbstractRpcServer extends AbstractRpcEntity implements RpcServer {
    protected String host;
    protected int port;
    protected ServiceProvider serviceProvider = SingletonFactory.getInstance(DefaultServiceProvider.class);
    protected ServiceRegistry serviceRegistry;

    @Override
    public void start() {
        throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.registerService(serviceName, new InetSocketAddress(host, port));
    }

    public AbstractRpcServer(){
        super();
        Class<?> bootClass = ReflectUtil.getBootClassByStackTrace();
        if(bootClass.isAnnotationPresent(ServiceScan.class)){
            scanService(bootClass);
        }
    }

    protected void scanService(Class<?> bootClass){
        String bootClassName = bootClass.getName();

        String basePackage = bootClass.getAnnotation(ServiceScan.class).value();
        /*
        “.”表示以启动类所在包作为扫描包
         */
        if("".equals(basePackage)){
            basePackage = bootClassName.substring(0, bootClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz: classes){
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj = null;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建服务类 {} 失败", clazz.getCanonicalName(), e);
                }
                if("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i : interfaces){
                        publishService(obj, i.getCanonicalName());
                    }
                }else{
                    publishService(obj, serviceName);
                }
            }
        }
    }
}
