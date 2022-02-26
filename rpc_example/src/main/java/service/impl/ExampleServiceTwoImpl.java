package service.impl;

import annotation.Service;
import service.ExampleServiceTwo;

@Service(name = "service_two")
public class ExampleServiceTwoImpl implements ExampleServiceTwo {
    @Override
    public String doService(String input) {
        return "Server: " + input;
    }
}
