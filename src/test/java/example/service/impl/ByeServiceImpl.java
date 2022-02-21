package example.service.impl;

import example.service.ByeService;
import rpc_core.annotation.Service;

@Service(name = "bye")
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye: " + name;
    }
}
