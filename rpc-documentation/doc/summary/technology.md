# 相关技术收集

## 通讯相关

### 序列化协议

#### 文本
- JSON
- XML

#### 二进制
- **protobuf**
- **kryo**
- FST
- hessian2

性能: kryo > protobuf

## 处理模式
- **reactive**

### IO处理
- **IOCP**
- **netty**
- **epoll**
- **Zero Copy**
- **Zero Allocation**

## IO模型
- **nio**
- **aio**

### 通信
- p2p
- http/2
- thrift
- hessian
- mina
- grizzly

## 服务注册相关
- muticast 注册中心
- **Consul** 注册中心
- **zookeeper** 注册中心
- redis 注册中心
- simple 注册中心
- directUrl（p2p)

## 容器相关
- jetty

## 扩展方式
- **SPI机制**
- spring配置

## 本地存根