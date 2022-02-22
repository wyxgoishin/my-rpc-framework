package rpc_core.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index = 0;

    @Override
    public <T> T select(List<T> inputs) {
        if(index >= inputs.size()){
            index %= inputs.size();
        }
        return inputs.get(index);
    }
}
