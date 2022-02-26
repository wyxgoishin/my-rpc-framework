package remoting.transport;

import annotation.PropertySource;
import codec.compressor.Compressor;
import codec.serializer.Serializer;
import enumeration.CompressorEnum;
import enumeration.SerializerEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.AbstractRegistry;
import registry.Registry;
import util.NacosUtil;
import util.PropertyFileUtil;
import util.ReflectUtil;
import util.ZkUtil;

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
    protected Class<?> registryUtilClass;

    public AbstractRpcEntity(){
        Class<?> bootClass = ReflectUtil.getBootClassByStackTrace();
        if(!bootClass.isAnnotationPresent(PropertySource.class)){
            log.error("boot class {} must be annotated with @PropertySource to enable initialize it from property file", bootClass.getName());
            throw new RuntimeException(String.format("boot class %s must be annotated with @PropertySource to enable initialize it from property file", bootClass.getName()));
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
            throw new RuntimeException("empty property path");
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
                if(property != null || fieldName.equals("serviceRegistry") || fieldName.equals("serviceDiscovery")){
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
                                String name = ((String) PropertyFileUtil.loadPropertyFromFile(propertyPath, "registry.type")).toLowerCase();
                                if(name.equals("nacos")){
                                    registryUtilClass = NacosUtil.class;
                                }else if(name.equals("zookeeper")){
                                    registryUtilClass = ZkUtil.class;
                                }else{
                                    throw new RuntimeException(String.format("unsupported type of service registry / discovery: %s", name));
                                }
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
                        throw new RuntimeException(String.format("failed to set some fields of boot class %s", bootClass.getName()));
                    }
                }
            }
        }else{
            throw new RuntimeException(String.format("unsupported property file type: %s", propertyPath));
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
