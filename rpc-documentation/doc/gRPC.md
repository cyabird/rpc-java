|功能列表              | 描述          |
|---------------------|---------------|
|Server端             |远程方法被调用方|
|Client端             |远程方法调用方  |

## 技术细节
### 服务端和客户端代码生成
通过 **protocol buffer** 编译器 **protoc** 来生成创建应用所需的客户端和服务端的代码,生成的代码同时包括客户端的存根和服务端要实现的抽象接口

#### 数据通讯
**service RPC** 由 **Protocol Buffer** 定义,基于 **HTTP/2.0** 实现.

### 序列化协议
**Protocol Buffer** 
- 特点:
  - 紧凑的二进制数据消息,更小的传输体积
    - **Varint**(有符号)和**Zigzag**(无符号)方式表示数值 
    - **Message Buffer** 方式表示field
  - 快速封解包
  
  
### Server端
server端通过侦听指定 **port**，等待Client链接请求，使用Netty构建。

### Client端
基于Netty实现，通过与Server建立TCP长链接，发送请求。Request与Response被封装成HTTP2的stream Frame。

### IO模型(selector)
1. 创建ServerBootstrap，设定BossGroup与workerGroup线程池
2. 注册childHandler，处理客户端链接中的请求成帧
3. bind到指定的port，即内部初始化ServerSocketChannel等，开始侦听和接受客户端链接。
4. BossGroup中的线程用于accept客户端链接，并转发（轮循）给workerGroup中的线程
5. workerGroup中的特定线程用于初始化客户端链接，初始化pipeline和handler，并将其注册到worker线程的selector上（每个worker线程持有一个selector，不共享）
6. selector上发生读写事件后，获取事件所属的链接句柄，然后执行handler（inbound），同时进行拆封package，handler执行完毕后，数据写入通过，由outbound handler处理（封包）通过链接发出。(每个worker线程上的数据请求是队列化的)

### stub对象
用于交互的存根对象
