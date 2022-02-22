package rpc_example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {
    private static final long serialVersionUID = 4015475395529376850L;
    private Integer id;
    private String message;
}
