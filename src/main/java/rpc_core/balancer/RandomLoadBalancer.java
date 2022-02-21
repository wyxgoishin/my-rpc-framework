package rpc_core.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private static final Random random = new Random(RandomLoadBalancer.class.hashCode());

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(random.nextInt(instances.size()));
    }
}
