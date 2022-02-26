# my-rpc-framework

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/wyxgoishin/my-rpc-framework)
![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=8&color=blue)

[中文](README.md) | English

## Introduction

[my-rpc-framework](https://github.com/wyxgoishin/my-rpc-framework) is a basic Java [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) framework based on Netty + Nacos / Zookeeper。

### Framework

![rpc-architecture](images/rpc-architure.png)

Server registers its service to Registry and the Client makes service call to Server after geting corresponding message about service from Registry.

### Features

- Supports BIO (Socket based) and NIO (Netty based) network transmission;
- Support configurable registry center including Nacos and Zookeeper;
- Self-defined transmission protocol with multiple configurable serializer (Json, Kryo, Protobuf, Hessian2) and multiple compression mechanism (None, GZip);
- Supports multiple property configuration approach (explicit initialization and property file with annotation);
- Supports batch service registry, customize service name and auto deregistere service when Server is shutdown;
- Supports multiple load balancer (random, roundup, hash-consistency) and Netty Channel reuse for Client;
- Well-defined interfaces with low module coupling.

## Project Module

- rpc_common: Enums, utils and so on;
- rpc_core: key classes for RPC framework;
- rpc_example: example codes for Server, Client and Service;

## Transmission Protocol

The transmission pack between Server and Client is encoded as below.

```
+---------------+---------------+-----------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Compressor Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-----------------+-------------+
|                                    Data Bytes                                   |
|                              Length: ${Data Length}                             |
+-------------------------------------------------------------------+-------------+
```

|    Parameter    |     Length     |                  Meaning                   |
| :-------------: | :------------: | :----------------------------------------: |
|  Magic Number   |    4 bytes     |   To show it's a valid pack, 0xDEADBABE    |
|  Package Type   |    4 bytes     | To show whether it's a request or response |
| Serializer Type |    4 bytes     |          Serializer for the data           |
| Compressor Type |    4 bytes     |          Compressor for the data           |
|   Data Length   |    4 bytes     |            Data length in byte             |
|   Data Bytes    | ${Data Length} |                    Data                    |

## Usage

### Download and run Nacos / Zookeeper

- Nacos

Download Nacos from [official site](https://github.com/alibaba/nacos/releases). Below takes `nacos-server-1.0.0.zip` as example.

```bash
unzip nacos-server-1.0.0.zip
cd nacos/bin 
```

For Linux / Unix / Mac platform, Run Nacos standalone with codes below:

```bash
sh startup.sh -m standalone
```

For Windows platform，Run Nacos standalone with codes below or double click `startup.cmd`:

```bash
startup.cmd -m standalone
```

For more information, please refer to [quick-start](https://nacos.io/en-us/docs/quick-start.html) 。

- Zookeeper

Download Zookeeper from [offcial site](https://zookeeper.apache.org/releases.html). Below takes `apache-zookeeper-3.6.3.zip` as example.

```bash
unzip apache-zookeeper-3.6.3.zip
cd apache-zookeeper-3.6.3
```

copy `zoo_sample.cfg` under directory `conf` to `zoo.cfg`. It's recommed for you to change the `dataDir` and `dataLogDir` properties in `zoo.cfg` than using default value.

```bash
cp conf/zoo_sample.cfg conf/zoo.cfg
```

For Linux / Unix / Mac platform, Run Zookeeper standalone with codes below:

```bash
bin/zkServer.sh start conf/zoo.cfg
```

For Windows platform，Run Zookeeper standalone with codes below:

```bash
bin/zkServer.cmd start conf/zoo.cfg
```

If you wish to run Zookeeper in cluster mode in one computer, you can create multiple `zoo_x.cfg` like `zoo_1.cfg` and create myid file with content as its cluster id like 1. Below is an example of `zoo_x.cfg` :

```bash
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/xxx/data
# Different Zookeeper instance should have different clientPort as they are run in same computer
clientPort=2182
dataLogDir=/xxx/log
# x is the cluster number for corresponding Zookeeper instance
server.1=localhost:2287:3387
server.2=localhost:2288:3388
server.3=localhost:2289:3389
```

Then start all the Zookeeper instance as below.

```bash
bin/zkServer.sh start conf/zoo_x.cfg
```

For more information, please refer to [official docs](https://zookeeper.apache.org/doc/current/index.html).

### Server

```java
// use annotation to initialize server from property file(*.yml, *.yaml, *.properties)
@PropertySource("server.properties")
public class xxxServer {
    public static void main(String[] args) {
        /* initialize server explicitly
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

### Client

```java
// use annotation to initialize client from property file(*.yml, *.yaml, *.properties)
@PropertySource("client.yaml")
public class xxxClient {
    public static void main(String[] args) {
        /* initialize client explicitly
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

### Property File

Currently only `.yml`, `.yaml`, `.properties` property files are supproted and the property files should locates in classpaths.

- YAML

```yaml
# host and port is only needed for server
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
# host and port is only needed for server
host=localhost
port=9000
serializer=kryo
compressor=gzip
registry.type=zookeeper
registry.serverAddress=127.0.0.1:2181
```

### Service

#### Register service

```java
// scan and register service with @Service annotated under given package
@ServiceScan("service.impl")
public class xxxServer{
    ...;
    
    // register service explicitly
    xxxService service = new xxxServiceImpl();
    for(Class<?> clazz : service.getClass().getInterfaces()){
        server.publishService(service, clazz.getCanonicalName());
    }
}

// default name for service is its canonical class Name
@Service(name = "xxx")
public class xxxServiceImpl implements xxxService{}
```

#### Make service call

```java
public class xxxClient{
    ...;
    
    // rpcClientProxy.setServiceName(serviceName);
    // if name not set, service is called with default name
    xxxService service = new rpcClientProxy.getProxy(xxxService.class);
    XXX result = service.method(parameters);
}
```

