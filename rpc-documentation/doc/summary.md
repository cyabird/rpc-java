## RPC相关功能
- 服务暴露
- 服务发现

## 技术

### 序列化相关
- **protobuf**
- **kryo**
- FST
- hessian2

#### 性能
kryo > protobuf

### 通讯相关
- **netty**
- **epoll**
- p2p
- http/2
- hessian
- thrift
- mina
- grizzly

### 服务注册相关
- muticast 注册中心
- **Consul** 注册中心
- **zookeeper** 注册中心
- redis 注册中心
- simple 注册中心
- directUrl（p2p)

### 容器相关
- jetty

### 扩展方式
- **SPI机制**
- spring配置

### 本地存根
