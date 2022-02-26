package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegistryEnum {
    NACOS_SERVICE_REGISTRY("nacos", "registry"),
    NACOS_SERVICE_DISCOVERY("nacos", "discovery"),
    ZK_SERVICE_REGISTRY("zookeeper", "registry"),
    ZK_SERVICE_DISCOVERY("zookeeper", "discovery");

    private final String name;
    private final String type;
}
