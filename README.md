# sRPC

![Static Badge](https://img.shields.io/badge/license-MIT-green) ![Static Badge](https://img.shields.io/badge/OpenJDK-21-blue) [![Build Status](https://app.travis-ci.com/histevehu/sRPC.svg?token=fsyLx7wqz3Fwpzu63a5T&branch=main)](https://app.travis-ci.com/histevehu/sRPC) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://makeapullrequest.com)

sRPC, a RPC framework made by Steve.

## 特性

- 实现了基于 Java 原生 Socket 传输与 Netty 传输两种网络传输方式
- 实现了四种序列化算法，Json 方式、Kryo 算法、Hessian 算法与 Google Protobuf 方式
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 实现自定义的通信协议

## 项目模块结构

- **sRPC-api** ：服务接口
- **sRPC-common** ：实体对象、工具类等公用类
- **sRPC-core** ：sRPC框架的核心实现
- **testClient** ：测试用客户端
- **testServer** ：测试用服务端