# sRPC

![Static Badge](https://img.shields.io/badge/license-MIT-green) ![Static Badge](https://img.shields.io/badge/OpenJDK-21-blue) [![Build Status](https://app.travis-ci.com/histevehu/sRPC.svg?token=fsyLx7wqz3Fwpzu63a5T&branch=main)](https://app.travis-ci.com/histevehu/sRPC) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://makeapullrequest.com)

sRPC，一个基于 Netty 和 Nacos 实现的 RPC 框架

## 特性

- 支持了基于 Java 原生 Socket 传输与 Netty 传输两种网络传输方式
- 支持四种序列化框架，Json、Kryo、Hessian 和 Google Protobuf
- 实现心跳机制和Channel复用，提供稳定和低开销的连接
- 实现了两种负载均衡算法：随机算法与轮转算法
- 实现自定义传输协议
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 支持注解式声明服务及自动扫描注册
- 良好的接口抽象，模块耦合度低，网络传输、序列化器、负载均衡算法可自由配置

## 架构

<img src="./docs/img/architecture.png" alt="sRPC架构" style="zoom:30%;" />

## 模块概览

- **sRPC-api** ：服务接口
- **sRPC-common** ：实体对象、工具类等公用类
- **sRPC-core** ：sRPC框架的核心实现
- **testClient** ：测试用客户端
- **testServer** ：测试用服务端

## 传输协议（SRTP）

调用及其结果的传输采用了SRTP协议（sRPC Transport Protocol）

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

| 字段              | 含义                                                                                             |
|:----------------|:-----------------------------------------------------------------------------------------------|
| Magic Number    | 魔数，标识这是一个 SRTP 协议包，默认为常量0x981017                                                               |
| Package Type    | 包类型，标识数据包是调用请求还是调用响应                                                                           |
| Serializer Type | 序列化器类型，标识这个包的数据部分的序列化方式                                                                        |
| Data Length     | 数据部分字节的长度                                                                                      |
| Data Bytes      | 数据部分。传输的对象，通常是一个`RpcRequest`或`RpcClient`对象，取决于`Package Type`字段，对象的序列化方式取决于`Serializer Type`字段。 |

## 使用

### 服务端

#### 下载运行 Nacos Server

以 Nacos 2.3.2 为例

```shell
wget https://github.com/alibaba/nacos/releases/download/2.3.2/nacos-server-2.3.2.tar.gz
tar -xvf nacos-server-2.3.2.tar.gz
cd nacos/bin
sh startup.sh -m standalone
```

#### 定义并实现调用接口

```java
public interface HelloService {
    String hello(String name);
}
```

```java
@SrpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```

#### 编写启动类

以Netty方式为例，默认采用Kryo序列化方式

```java
@SrpcServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9000);
        server.start();
    }
}
```

### 消费端

#### 调用服务

默认采用轮转负载均衡算法

```java
public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String res = helloService.hello("Hello World!");
        System.out.println(res);
    }
}
```

## LICENSE

sRPC is under the MIT license. See the [LICENSE](https://github.com/histevehu/sRPC/blob/main/LICENSE)) file for details.