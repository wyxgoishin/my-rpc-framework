package snakeYamlTest;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class SnakeYamlTest {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        InputStream inputStream = SnakeYamlTest.class.getClassLoader().getResourceAsStream("server.yaml");
//        Map<String, Object> obj = yaml.load(inputStream);
        YamlTestObject obj = yaml.loadAs(inputStream, YamlTestObject.class);
        System.out.println(obj);
    }
}
