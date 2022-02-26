package exception;

import enumeration.RpcExceptionBean;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcException extends RuntimeException{
    private static final long serialVersionUID = 8123785679521392259L;
    private String errorMessage;

    public RpcException(RpcExceptionBean rpcExceptionBean){
        this.errorMessage = rpcExceptionBean.getErrorMessage();
    }
}
