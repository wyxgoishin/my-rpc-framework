package rpc_common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
// 因为 kryo 与 RpcRequest绑定要实例化它，而 builder 模式下无法这样做
//@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -1957686175929610806L;
    /*
    待调用服务接口名称，默认为接口的名称
     */
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String requestId;

    /* out.writeObject(clazz)会报错，暂时没查明原因
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(interfaceName);
        out.writeUTF(methodName);
        out.writeInt(parameters.length);
        for(Class<?> clazz : parameterTypes){
            out.writeObject(clazz);
        }
        out.writeInt(parameters.length);
        for(Object parameter : parameters){
            out.writeObject(parameter);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.interfaceName = in.readUTF();
        this.methodName = in.readUTF();
        this.parameterTypes = new Class<?>[in.readInt()];
        for(int i = 0; i < this.parameterTypes.length; i++){
            this.parameterTypes[i] = (Class<?>) in.readObject();
        }
        this.parameters = new Object[in.readInt()];
        for(int i = 0; i < this.parameters.length; i++){
            this.parameters[i] = in.readObject();
        }
    }

     */

    /* 手动实现的builder模式，仅做学习参考

    public static class Builder{
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

     */
}
