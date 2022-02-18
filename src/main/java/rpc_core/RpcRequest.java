package rpc_core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RpcRequest implements Externalizable {
    private static final long serialVersionUID = -1957686175929610806L;
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    static class Builder{
        private String interfaceName;
        private String methodName;
        private Class<?>[] parameterTypes;
        private Object[] parameters;

        public Builder(){}

        public Builder interfaceName(String interfaceName){
            this.interfaceName = interfaceName;
            return this;
        }

        public Builder methodName(String methodName){
            this.methodName = methodName;
            return this;
        }

        public Builder parameterTypes(Class<?>[] parameterTypes){
            this.parameterTypes = parameterTypes;
            return this;
        }

        public Builder parameters(Object[] parameters){
            this.parameters = parameters;
            return this;
        }

        public RpcRequest build(){
            return new RpcRequest(this);
        }
    }

    public RpcRequest(Builder builder){
        this.interfaceName = builder.interfaceName;
        this.methodName = builder.methodName;
        this.parameterTypes = builder.parameterTypes;
        this.parameters = builder.parameters;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(interfaceName);
        out.writeUTF(methodName);
        out.writeObject(parameterTypes);
        out.writeObject(parameters);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.interfaceName = in.readUTF();
        this.methodName = in.readUTF();
        this.parameterTypes = (Class<?>[]) in.readObject();
        this.parameters = (Object[]) in.readObject();
    }
}
