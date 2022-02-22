package rpc_core.remoting.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_common.enumeration.CompressorEnum;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.enumeration.SerializerEnum;
import rpc_common.exception.RpcException;
import rpc_common.util.PropertyFileUtil;
import rpc_common.util.ReflectUtil;
import rpc_core.annotation.PropertySource;
import rpc_core.codec.compressor.Compressor;
import rpc_core.registry.AbstractRegistry;
import rpc_core.registry.Registry;
import rpc_core.codec.serializer.Serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AbstractRpcEntity implements RpcEntity{
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    protected Serializer serializer;
    protected static final SerializerEnum DEFAULT_SERIALIZER = SerializerEnum.KRYO;
    protected Compressor compressor;
    protected static final CompressorEnum DEFAULT_COMPRESSOR = CompressorEnum.NONE;

    public AbstractRpcEntity(){
        Class<?> bootClass = ReflectUtil.getBootClassByStackTrace();
        if(!bootClass.isAnnotationPresent(PropertySource.class)){
            log.error(RpcExceptionBean.BOOT_SERVER_FAILED.getErrorMessage());
            throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
        }else{
            String propertyPath = bootClass.getAnnotation(PropertySource.class).value();
            Object properties = PropertyFileUtil.readPropertyFromFile(propertyPath);
            PropertyFileUtil.putProperties(propertyPath, properties);
            initializeFromPropertySource(this, bootClass);
        }
    }

    public void initializeFromPropertySource(RpcEntity rpcServer, Class<?> bootClass){
        String propertyPath = bootClass.getAnnotation(PropertySource.class).value();
        if(propertyPath.equals("")){
            throw new RuntimeException("无效的配置文件地址");
        }
        if(propertyPath.endsWith(".yaml") || propertyPath.endsWith(".yml") || propertyPath.endsWith(".properties")){
            List<Field> fields = new ArrayList<>();
            Class<?> clazz = rpcServer.getClass();
            while(clazz != null && clazz != Object.class){
                Field[] declaredFields = clazz.getDeclaredFields();
                fields.addAll(Arrays.asList(declaredFields));
                clazz = clazz.getSuperclass();
            }
            for(Field field : fields){
                String fieldName = field.getName();
                Object property = PropertyFileUtil.loadPropertyFromFile(propertyPath, fieldName);
                if(fieldName.equals("serviceRegistry") || fieldName.equals("serviceDiscovery")){
                    property = PropertyFileUtil.loadPropertyFromFile(propertyPath, "registry.type");
                }
                if(property != null){
                    try {
                        field.setAccessible(true);
                        switch (fieldName) {
                            case "serializer":
                                String serializerName = ((String) property).toLowerCase();
                                Serializer serializer = Serializer.getByName(serializerName);
                                field.set(rpcServer, serializer);
                                break;
                            case "compressor":
                                String compressorName = ((String) property).toLowerCase();
                                Compressor compressor =  Compressor.getByName(compressorName);
                                field.set(rpcServer, compressor);
                                break;
                            case "serviceRegistry":
                            case "serviceDiscovery":
                                String name = ((String) property).toLowerCase();
                                String type = fieldName.substring(7).toLowerCase();
                                String serverAddress = (String) PropertyFileUtil.loadPropertyFromFile(propertyPath, "registry.serverAddress");
                                AbstractRegistry registry = Registry.getRegistryByNameAndType(name, type);
                                registry.setServerAddress(serverAddress);
                                field.set(rpcServer, registry);
                                break;
                            case "port":
                                property = Integer.parseInt((String) property);
                                field.set(rpcServer, property);
                                break;
                            default:
                                field.set(rpcServer, property);
                                break;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RpcException(RpcExceptionBean.BOOT_SERVER_FAILED);
                    }
                }
            }
        }else{
            throw new RuntimeException("不支持的配置文件格式");
        }
    }

    @Override
    public void setSerializer(SerializerEnum serializerEnum) {
        this.serializer = Serializer.getByEnum(serializerEnum);
    }

    @Override
    public void setSerializer(int serializerCode) {
        this.serializer = Serializer.getByCode(serializerCode);
    }

    @Override
    public void setSerializer(String serializerName) {
        this.serializer = Serializer.getByName(serializerName);
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serCompressor(CompressorEnum compressorEnum) {
        this.compressor = Compressor.getByEnum(compressorEnum);
    }

    @Override
    public void setCompressor(int compressorCode) {
        this.compressor = Compressor.getByCode(compressorCode);
    }

    @Override
    public void setCompressor(String compressorName) {
        this.compressor = Compressor.getByName(compressorName);
    }

    @Override
    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }
}
