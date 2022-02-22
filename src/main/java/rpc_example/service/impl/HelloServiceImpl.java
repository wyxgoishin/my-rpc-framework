package rpc_example.service.impl;


import rpc_example.service.HelloObject;
import rpc_example.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc_core.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        logger.info("来自 id 为 {} 的用户的消息：{}", helloObject.getId(), helloObject.getMessage());
        return helloObject.toString();
    }
}
