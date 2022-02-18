package service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        logger.info("从id为 {} 的用户处接受到：{}", helloObject.getId(), helloObject.getMessage());
        return helloObject.toString();
    }
}
