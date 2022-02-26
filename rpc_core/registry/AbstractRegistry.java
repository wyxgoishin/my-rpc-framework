package rpc_core.registry;

import lombok.Setter;

@Setter
public class AbstractRegistry implements Registry{
    protected String serverAddress;
}
