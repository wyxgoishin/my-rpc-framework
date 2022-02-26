package rpc_example.service.impl;

import rpc_core.annotation.Service;
import rpc_example.service.ExampleServiceTwo;

@Service(name = "service_two")
public class ExampleServiceTwoImpl implements ExampleServiceTwo {
    @Override
    public String doService(String input) {
        return "Server: " + input;
    }
}
