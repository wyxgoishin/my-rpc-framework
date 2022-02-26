package remoting.transport.server;

import annotation.Service;
import annotation.ServiceScan;
import enumeration.RpcExceptionBean;
import exception.RpcException;
import factory.SingletonFactory;
import provider.DefaultServiceProvider;
import provider.ServiceProvider;
import registry.service_registry.ServiceRegistry;
import remoting.transport.AbstractRpcEntity;
import util.ReflectUtil;

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
        "." means using the package where boot class locates as package name
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
                    log.error("create service instance {} failed", clazz.getCanonicalName(), e);
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
