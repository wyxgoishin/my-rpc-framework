package balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private static final Random random = new Random(RandomLoadBalancer.class.hashCode());

    @Override
    public <T> T select(List<T> inputs) {
        return inputs.get(random.nextInt(inputs.size()));
    }
}
