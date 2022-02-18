package service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.HelloObject;
import service.HelloService;

public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        logger.info("来自 id 为 {} 的用户的消息：{}", helloObject.getId(), helloObject.getMessage());
        return helloObject.toString();
    }
}
