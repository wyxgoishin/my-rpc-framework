# my-rpc-framework

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/wyxgoishin/my-rpc-framework)
![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=8&color=blue)

中文 | [English](README_EN.md)

## 介绍

[my-rpc-framework](https://github.com/wyxgoishin/my-rpc-framework) 是一款基于 Netty + Nacos / Zookeeper 实现的基础 Java [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) 框架。

### 架构

![rpc-architecture](images/rpc-architure.png)

服务提供端 Server 向注册中心注册服务，服务消费端 Client 通过注册中心拿到服务相关信息，然后再通过网络请求服务提供端 Server。

### 特性

- 支持 BIO（基于 Socket）和 NIO（基于 Netty）的网络传输方式
- 支持 Nacos、Zookeeper 作为可配置的注册中心，管理服务提供者信息
- 实现自定义传输协议，支持多种可配置的序列化机制（Json、Kryo、Protobuf、Hessian2）和压缩机制（无压缩、GZip）
- 支持多种属性配置方式（显示类初始化和注解+配置文件）
- 服务提供端支持基于注解的批量服务注册、服务自定义命名，当其失效时会自动注销对应服务
- 服务消费端支持多种负载均衡算法（随机、轮询、哈希一致性），在使用 Netty 进行通信时会复用 Channel
- 接口定义良好，模块耦合性低

## 项目模块概览

- rpc_common：存放一些枚举类、工具类...
- rpc_core：RPC 框架的核心实现类
- rpc_example：存放了服务端、客户端的代码示例

## 传输协议

服务端和消费端间传输数据包采取如下传输格式。

```
+---------------+---------------+-----------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Compressor Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-----------------+-------------+
|                                    Data Bytes                                   |
|                              Length: ${Data Length}                             |
+-------------------------------------------------------------------+-------------+
```

|      字段       |      长度      |                 含义                 |
| :-------------: | :------------: | :----------------------------------: |
|  Magic Number   |     4 字节     | 魔数，表明这是一个数据包，0xDEADBABE |
|  Package Type   |     4 字节     |   包类型，表明其为请求包还是响应包   |
| Serializer Type |     4 字节     |   序列器类型，表明数据的序列化方式   |
| Compressor Type |     4 字节     |   压缩算法类型，表明数据的压缩方式   |
|   Data Length   |     4 字节     |            数据的字节长度            |
|   Data Bytes    | ${Data Length} |             数据实体部分             |

## 使用

### 下载运行 Nacos / Zookeeper

- Nacos

从[官方网站](https://github.com/alibaba/nacos/releases)选择并下载你希望的版本的 Nacos，下面以 `nacos-server-1.0.0.zip` 为例。

```bash
unzip nacos-server-1.0.0.zip
cd nacos/bin 
```

对于 Linux / Unix / Mac 平台，运行以下代码来以单机模式启动 Nacos：

```bash
sh startup.sh -m standalone
```

对于 Windows 平台，运行以下代码或双击 `startup.cmd`来以单机模式启动 Nacos：

```bas
startup.cmd -m standalone
```

对于更多启动方式，请参照 [quick-start](https://nacos.io/en-us/docs/quick-start.html) 。

- Zookeeper

从[官方网站](https://zookeeper.apache.org/releases.html)选择并下载你希望的版本的 Zookeeper，下面以 `apache-zookeeper-3.6.3.zip`为例。

```bash
unzip apache-zookeeper-3.6.3.zip
cd apache-zookeeper-3.6.3
```

复制 `conf` 文件夹下的 `zoo_sample.cfg` 文件并重命名为 `zoo.cfg` ，并注意更改 `dataDir` 和 `dataLogDir` 配置。

```bash
cp conf/zoo_sample.cfg conf/zoo.cfg
```

对于 Linux / Unix / Mac 平台，运行以下代码来以单机模式启动 Zookeeper：

```bash
bin/zkServer.sh start conf/zoo.cfg
```

对于 Windows 平台，运行以下代码来以单机模式启动 Zookeeper：

```bash
bin/zkServer.cmd start conf/zoo.cfg
```

若希望以单台电脑上以集群模式运行 Zookeeper，可创建多个 `zoo_x.cfg`，并在对应的 `dataDir` 下分别创建 `myid` 文件，文件内容为其集群编号。下面是 `zoo_x.cfg` 的示例文件：

```bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/xxx/data
# 不同 Zookeeper 实例的端口应不同，因为它们运行在同一台电脑上
clientPort=2182
dataLogDir=/xxx/log
# x 为其集群编号，如 1
server.1=localhost:2287:3387
server.2=localhost:2288:3388
server.3=localhost:2289:3389
```

而后依次启动对应的 Zookeeper 实例：

```bash
bin/zkServer.sh start conf/zoo_x.cfg
```

### 服务端

```java
// 利用注解来初始化服务器
@PropertySource("server.properties")
public class xxxServer {
    public static void main(String[] args) {
        /* 显式初始化服务端
        String host = "localhost";
        int port = 9000;
        String serverAddress = "127.0.0.1:8848";
        ServiceRegistry serviceRegistry = new NacosServiceRegistry(serverAddress);
        SerializerEnum serializer = SerializerEnum.KRYO;
        CompressorEnum compressor = CompressorEnum.GZIP;
        RpcServer server = new SocketServer(host, port, serviceRegistry, serializer, compressor);
         */

        RpcServer server = new SocketServer();
        server.start();
    }
}
```

### 客户端

```java
// 利用注解来初始化客户端
@PropertySource("client.yaml")
public class xxxClient {
    public static void main(String[] args) {
        /* 显式初始化客户端
        String serviceAddress = "127.0.0.1:8848";
        LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        ServiceDiscovery serviceDiscovery = new NacosServiceDiscovery(serviceAddress, loadBalancer);
        SerializerEnum serializer = SerializerEnum.KRYO;
        CompressorEnum compressor = CompressorEnum.GZIP;
        RpcClient client = new SocketClient(serviceDiscovery, serializer, compressor);
         */

        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
    }
}
```

### 配置文件

目前仅支持 `.yml`、`.yaml` 和 `.properties` 类型的配置文件，且配置文件应当放在 `classpath` 目录下。

- YAML

```yaml
# host 和 port 属性只有服务端需要，其他属性客户端和服务端都需要
host: localhost
port: 9000
serializer: kryo
compressor: gzip
registry:
    type: nacos
    serverAddress: 127.0.0.1:8848
```

- Properties

```properties
# host 和 port 属性只有服务端需要，其他属性客户端和服务端都需要
host=localhost
port=9000
serializer=kryo
compressor=gzip
registry.type=zookeeper
registry.serverAddress=127.0.0.1:2181
```

### 服务

#### 服务注册

```java
// 自动扫描注册对应包下所有带有 @Service 注解的服务类
@ServiceScan("service.impl")
public class xxxServer{
    ...;
    
    // 显式注册服务
    xxxService service = new xxxServiceImpl();
    for(Class<?> clazz : service.getClass().getInterfaces()){
        server.publishService(service, clazz.getCanonicalName());
    }
}

// 服务名不填则默认为类的全限类名
@Service(name = "xxx")
public class xxxServiceImpl implements xxxService{}
```

#### 服务调用

```java
public class xxxClient{
    ...;
    
    // rpcClientProxy.setServiceName(serviceName);
    // 如果不显式指定服务名，则用默认服务名去发起服务调用
    xxxService service = new rpcClientProxy.getProxy(xxxService.class);
    XXX result = service.method(parameters);
}
```

