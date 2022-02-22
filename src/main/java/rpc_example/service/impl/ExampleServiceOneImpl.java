package rpc_example.service.impl;

import rpc_core.annotation.Service;
import rpc_example.service.ExampleServiceOne;

@Service
public class ExampleServiceOneImpl implements ExampleServiceOne {
    @Override
    public String doService() {
        return "This is a reply from example service one";
    }
}
