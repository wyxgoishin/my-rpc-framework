package rpc_common.util;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertyFileUtil {
    public static final Map<String, Object> propertyMap = new ConcurrentHashMap<>();

    public static <T> void putProperties(String propertyPath, T properties){
        propertyMap.put(propertyPath, properties);
    }

    public static <T> T getProperties(String propertyPath, Class<T> clazz){
        Object properties = propertyMap.get(propertyPath);
        return clazz.cast(properties);
    }

    public static Object loadPropertyFromFile(String propertyPath, String key){
        if(propertyPath.endsWith(".yaml") || propertyPath.endsWith(".yml")){
            return loadPropertyFromYml(propertyPath, key);
        }else if(propertyPath.endsWith(".properties")){
            return loadPropertyFromProperties(propertyPath, key);
        }else{
            throw new RuntimeException("不支持的配置文件类型");
        }
    }

    private static Object loadPropertyFromProperties(String propertyPath, String key){
        Properties properties = getProperties(propertyPath, Properties.class);
        return properties.getProperty(key);
    }

    private static Object loadPropertyFromYml(String propertyPath, String key) {
        String[] keys = key.split("\\.");
        Map map = getProperties(propertyPath, Map.class);
        Object value = null;
        for(int i = 0; i < keys.length; i++){
            if(map == null){
                return null;
            }else{
                value = map.get(keys[i]);
                map = i == keys.length - 1 ? null : (Map) value;
            }
        }
        return value;
    }

    public static Object readPropertyFromFile(String propertyPath){
        if(propertyPath.endsWith(".yaml") || propertyPath.endsWith(".yml")){
            return readPropertyFromYml(propertyPath);
        }else if(propertyPath.endsWith("properties")){
            return readPropertyFromProperties(propertyPath);
        }else{
            throw new RuntimeException("不支持的配置文件类型");
        }
    }

    private static Map<String, Object> readPropertyFromYml(String propertyPath){
        InputStream in = PropertyFileUtil.class.getClassLoader().getResourceAsStream(propertyPath);
        Yaml yaml = new Yaml();
        return yaml.load(in);
    }

    private static Properties readPropertyFromProperties(String propertyPath){
        try {
            InputStream in = PropertyFileUtil.class.getClassLoader().getResourceAsStream(propertyPath);
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("配置文件不存在");
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件错误");
        }
    }
}
