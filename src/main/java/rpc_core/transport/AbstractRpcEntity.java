package rpc_core.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import rpc_common.enumeration.CompressorCode;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerCode;
import rpc_common.exception.RpcException;
import rpc_common.util.ReflectUtil;
import rpc_core.annotation.PropertySource;
import rpc_core.annotation.ServiceScan;
import rpc_core.compresser.Compressor;
import rpc_core.registry.NacosServiceRegistry;
import rpc_core.registry.ServiceRegistry;
import rpc_core.serializer.Serializer;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class AbstractRpcEntity implements RpcEntity{
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    protected Serializer serializer;
    protected static final SerializerCode DEFAULT_SERIALIZER_CODE = SerializerCode.KRYO;
    protected Compressor compressor;
    protected static final CompressorCode DEFAULT_COMPRESSOR_CODE = CompressorCode.NONE;

    public AbstractRpcEntity(){
        String mainClassName = ReflectUtil.getBootClassByStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                return;
            }
        } catch (ClassNotFoundException e) {
            log.error("{}:", RpcExceptionBean.LOAD_BOOT_CLASS_FAILED.getErrorMessage(), e);
            throw new RpcException(RpcExceptionBean.LOAD_BOOT_CLASS_FAILED);
        }
        if(!startClass.isAnnotationPresent(PropertySource.class)){
            log.error(RpcExceptionBean.BOOT_SERVER_FAILED.getErrorMessage());
            throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
        }else{
            initializeFromPropertySource(this, startClass);
        }
    }

    public void initializeFromPropertySource(RpcEntity rpcServer, Class<?> startClass){
        String propertyPath = startClass.getAnnotation(PropertySource.class).value();
        if(propertyPath.equals("")){
            throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
        }
        if(propertyPath.endsWith(".yaml") || propertyPath.endsWith(".yml")){
            List<Field> fields = new ArrayList<>();
            Class<?> clazz = rpcServer.getClass();
            while(clazz != null && clazz != Object.class){
                Field[] declaredFields = clazz.getDeclaredFields();
                fields.addAll(Arrays.asList(declaredFields));
                clazz = clazz.getSuperclass();
            }
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(propertyPath);
            Yaml yaml = new Yaml();
            Map<String, Object> properties = yaml.load(in);
            for(Field field : fields){
                String fieldName = field.getName();
                Object property = properties.get(fieldName);
                if(property != null){
                    try {
                        field.setAccessible(true);
                        if(fieldName.equals("serializer")){
                            String serializerName = ((String) property).toLowerCase();
                            Serializer serializer;
                            if(serializerName.equals("json")){
                                serializer = Serializer.getByCode(SerializerCode.JSON);
                            }else if(serializerName.equals("kryo")){
                                serializer = Serializer.getByCode(SerializerCode.KRYO);
                            }else{
                                throw new RpcException(RpcExceptionBean.UNKNOWN_SERIALIZER);
                            }
                            field.set(rpcServer, serializer);
                        }else if(fieldName.equals("compressor")){
                            String compressorName = ((String) property).toLowerCase();
                            Compressor compressor;
                            if(compressorName.equals("none")){
                                compressor = Compressor.getByCode(CompressorCode.NONE);
                            }else if(compressorName.equals("gzip")){
                                compressor = Compressor.getByCode(CompressorCode.GZIP);
                            }else{
                                throw new RpcException(RpcExceptionBean.UNKNOWN_COMPRESSOR);
                            }
                            field.set(rpcServer, compressor);
                        }else if(fieldName.equals("serviceRegistry")){
                            String serviceRegistryName = ((String) property).toLowerCase();
                            ServiceRegistry serviceRegistry;
                            if(serviceRegistryName.equals("nacos")){
                                serviceRegistry = new NacosServiceRegistry();
                            }else{
                                throw new RpcException(RpcExceptionBean.UNKNOWN_SERVICE_REGISTRTY);
                            }
                            field.set(rpcServer, serviceRegistry);
                        }else{
                            field.set(rpcServer, property);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
                    }
                }
            }
        }else{
            throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
        }
    }

    @Override
    public void setSerializer(SerializerCode serializerCode) {
        this.serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public void setSerializer(int serializerCode) {
        this.serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serCompressor(CompressorCode compressorCode) {
        this.compressor = Compressor.getByCode(compressorCode);
    }

    @Override
    public void setCompressor(int compressorCode) {
        this.compressor = Compressor.getByCode(compressorCode);
    }

    @Override
    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }
}
