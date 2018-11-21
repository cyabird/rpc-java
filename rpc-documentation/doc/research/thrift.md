| 特点              |
|-------------------|
|基于二进制的编解码  |
|基于NIO的底层通信   |
|使用IDL支持跨平台调用|

## thrift架构

![](image/thrift-framework.jpg)

| 组件     |描述                           |
|----------|-------------------------------|
|TProtocol |协议和编解码组件                |
|TTransport|传输组件                        |
|TProcessor|服务调用组件                    |
|TServer   |服务端组件                      |
|TClient   |客户端组件                      |
|IDL       |服务描述组件，负责生产跨平台客户端|

黄色部分是用户实现的业务逻辑.

褐色部分是根据Thrift定义的服务接口描述文件生成的客户端和服务器端代码框架.

红色部分是根据Thrift文件生成代码实现数据的读写操作。

红色部分以下是 Thrift 的传输体系、协议以及底层I/O通信，可以定义一个服务选择不同的传输协议和传输层而不用重新生成代码。

> Thrift服务器包含用于绑定协议和传输层的基础架构，提供阻塞、非阻塞、单线程和多线程的模式运行在服务器上，可以配合服务器 / 容器一起运行，可以和现有的 J2EE 服务器 /Web 容器无缝的结合。

### TProtocol
1. 写消息头(writeMessageBegin)。Message里面定义方法名，调用的类型，版本号，消息seqId
2. 写消息体,包含方法的参数。类(writeStructBegin)、字段(writeFieldBegin)、顺序号(从１开始)、集合(writeListBegin/writeMapBegin)，每个复杂的数据类型写完都会调用writeXXXEnd，直到writeMessageEnd结束。
3. 读消息时根据数据类型读取相应的长度

#### 支持协议
- TBinaryProtocol —— 二进制编码格式
- TCompactProtocol —— 高效率的、密集的二进制编码格式
- TJSONProtocol —— 使用 JSON 的数据编码协议
- TSimpleJSONProtocol —— 只提供 JSON 只写的协议，适用于通过脚本语言解析

## TTransport
- TSocket —— 使用阻塞式 I/O 进行传输，是最常见的模式
- TFramedTransport —— 使用非阻塞方式，按块的大小进行传输，类似 NIO。若使用 TFramedTransport 传输层，其服务器必须修改为非阻塞的服务类型。
- TNonblockingTransport —— 使用非阻塞方式，用于构建异步客户端

## TServer
- TSimpleServer —— 单线程服务器端使用标准的阻塞式 I/O
- TThreadPoolServer —— 多线程服务器端使用标准的阻塞式 I/O
- TNonblockingServer —— 多线程服务器端使用非阻塞式 I/O

## IDL
支持跨语言环境调用的一个服务描述组件，一般都是采用文本格式来定义,自动生成辅助代码，包括客户端代码和序列化接口的代码.

生成部分:
- 接口类型(同步调用的Iface、异步调用的AsyncIface) —— 服务器和客户端共同使用。服务器端做顶层接口，编写实现类。客户端代码使用它作为生成代理的服务接口。
- 客户端类型 —— 一个同步调用的客户端Client，一个异步调用的客户端AsyncClient.
- Processor —— 用来支持方法调用，每个服务的实现类使用Processor来注册，保证服务器端调用接口实现时能定位到具体的实现类。
- 方法参数的封装类，以"方法名_args"命名
- 方法返回值的封装类，以"方法名_result"命名

## 方法调用流程
1. 自动生成的Iface接口，是远程方法的顶层接口。
2. 自动生成的Processor类及相关父类，包括TProcessor接口，TBaseProcess抽象类。
3. ProcessFunction抽象类，抽象了一个具体的方法调用，包含了方法名信息，调用方法的抽象过程等
4. TNonblcokingServer，是NIO服务器的默认实现，通过Args参数来配置Processor等信息
5. FrameBuffer类，服务器NIO的缓冲区对象，这个对象在服务器端收到全包并解码后，会调用Processor去完成实际的方法调用
6. 服务器端的方法的具体实现类，实现Iface接口