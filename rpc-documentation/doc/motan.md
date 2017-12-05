| 角色                | 描述                             | 
|--------------------|----------------------------------|
|注册中心(Registry)   |服务注册与发现的注册中心            |
|服务提供方(RPC Server) |暴露服务的服务提供方                |
|服务消费方(RPC Client) |调用远程服务的服务消费方            |

![](image/motan-architecture.jpg)

|功能列表 | 描述                |
|---------|---------------------|
|注册中心 |服务注册与发现的注册中心|
|Server端 |服务提供方            |
|Client端 |服务调用方            |

## 架构模块

![](image/motan-model.jpg)

### 公用相关模块

#### register模块

和注册中心进行交互，进行注册服务、订阅服务、服务变更通知、服务心跳发送等功能；
- Server 端会在系统初始化时通过 register 模块注册服务;
- Client 端在系统初始化时会通过 register 模块订阅具体服务的 Server 列表，当 Server 列表发生变更时也由 register 模块通知 Client。

#### protocol模块

用于进行 RPC 服务的描述和 RPC 服务的配置管理，及自定义 filter。

#### serialize模块

将 RPC 请求中的对象进行序列化与反序列化(默认hessian2);

#### transport模块

远程通信，默认 Netty NIO 的 TCP 长链接方式.

### Client相关模块

#### cluster模块


## 流程

### 客户端流程

![](image/motan-client.jpg)

### 服务端流程

![](image/motan-server.jpg)