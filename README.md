# sRPC

![Static Badge](https://img.shields.io/badge/license-MIT-green) ![Static Badge](https://img.shields.io/badge/OpenJDK-21-blue) [![Build Status](https://app.travis-ci.com/histevehu/sRPC.svg?token=fsyLx7wqz3Fwpzu63a5T&branch=main)](https://app.travis-ci.com/histevehu/sRPC) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://makeapullrequest.com)

sRPC, a RPC framework made by Steve.

## 特性

- 实现了基于 Java 原生 Socket 传输与 Netty 传输两种网络传输方式
- 实现了四种序列化算法，Json 方式、Kryo 算法、Hessian 算法与 Google Protobuf 方式
- 实现心跳机制和Channel复用，提供稳定和低开销的连接
- 实现了两种负载均衡算法：随机算法与轮转算法
- 实现自定义传输协议
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 支持注解式声明服务及自动扫描注册
- 良好的接口抽象，模块耦合度低，网络传输、序列化器、负载均衡算法可自由配置

## 模块概览

- **sRPC-api** ：服务接口
- **sRPC-common** ：实体对象、工具类等公用类
- **sRPC-core** ：sRPC框架的核心实现
- **testClient** ：测试用客户端
- **testServer** ：测试用服务端

## 传输协议（SRTP）

调用及其结果的传输采用了SRTP协议（sRPC Transport Protocol）

调用参数与返回值的传输采用了如下 MRF 协议（ My-RPC-Framework 首字母）以防止粘包：

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

## LICENSE

sRPC is under the MIT license. See the [LICENSE](https://github.com/histevehu/sRPC/blob/main/LICENSE)) file for details.