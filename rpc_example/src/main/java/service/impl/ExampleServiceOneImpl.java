package service.impl;

import annotation.Service;
import service.ExampleServiceOne;

@Service
public class ExampleServiceOneImpl implements ExampleServiceOne {
    @Override
    public String doService() {
        return "This is a reply from example service one";
    }
}
