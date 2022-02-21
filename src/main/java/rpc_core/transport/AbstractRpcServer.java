package rpc_core.transport;

import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_common.util.ReflectUtil;
import rpc_core.provider.DefaultServiceProvider;
import rpc_core.provider.ServiceProvider;
import rpc_core.registry.ServiceRegistry;
import rpc_core.annotation.Service;
import rpc_core.annotation.ServiceScan;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer extends AbstractRpcEntity implements RpcServer {
    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider = SingletonFactory.getInstance(DefaultServiceProvider.class);

    @Override
    public void start() {
        throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

    public void scanService(){
        String mainClassName = ReflectUtil.getBootClassByStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                return;
            }
        } catch (ClassNotFoundException e) {
            logger.error("{}:", RpcExceptionBean.LOAD_BOOT_CLASS_FAILED.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.LOAD_BOOT_CLASS_FAILED);
        }

        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        /*
        “.”表示以启动类所在包作为扫描包
         */
        if("".equals(basePackage)){
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz: classes){
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj = null;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建服务类 {} 失败", clazz.getCanonicalName(), e);
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
