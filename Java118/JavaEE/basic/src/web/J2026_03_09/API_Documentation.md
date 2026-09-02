# J2026_03_09 HTTP Server/Client API 文档

## 目录结构
```
src/
└── web/
    └── J2026_03_09/
        ├── model/
        │   ├── HttpRequest.java
        │   └── HttpResponse.java
        ├── server/
        │   └── BaseHttpServer.java
        └── client/
            └── HttpClient.java
```

## 类功能说明

### 1. HttpRequest (model/HttpRequest.java)

表示HTTP请求对象，解析并存储请求信息。

#### 主要方法：
- `parse(String rawRequest)` - 解析原始HTTP请求字符串为HttpRequest对象
- `getMethod()` - 获取HTTP方法（GET/POST等）
- `getPath()` - 获取请求路径
- `getHeaders()` - 获取请求头集合
- `getBody()` - 获取请求体
- `getHeader(String key)` - 获取指定请求头值
- `toString()` - 返回请求对象的字符串表示

#### 属性：
- `method` - HTTP方法
- `path` - 请求路径
- `headers` - 请求头映射
- `body` - 请求体

---

### 2. HttpResponse (model/HttpResponse.java)

表示HTTP响应对象，构建并发送响应给客户端。

#### 主要方法：
- `ok(String body)` - 创建200 OK响应
- `notFound(String body)` - 创建404 Not Found响应
- `internalServerError(String body)` - 创建500 Internal Server Error响应
- `badRequest(String body)` - 创建400 Bad Request响应
- `getStatusCode()` - 获取状态码
- `getStatusMessage()` - 获取状态消息
- `getBody()` - 获取响应体
- `toBytes()` - 将响应转换为字节数组
- `addHeader(String key, String value)` - 添加响应头

#### 属性：
- `statusCode` - HTTP状态码
- `statusMessage` - 状态消息
- `headers` - 响应头映射
- `body` - 响应体

---

### 3. BaseHttpServer (server/BaseHttpServer.java)

基础HTTP服务器类，提供TCP套接字服务端框架。

#### 主要方法：
- `start()` - 启动服务器，开始监听端口
- `stop()` - 停止服务器
- `isRunning()` - 检查服务器是否运行
- `getPort()` - 获取服务器端口号
- `handleRequest(HttpRequest request, String clientInfo)` - 抽象方法，处理HTTP请求（需子类实现）

#### 属性：
- `port` - 服务器端口号
- `poolSize` - 线程池大小
- `serverSocket` - 服务器套接字
- `threadPool` - 线程池
- `running` - 服务器运行状态

#### 内部类 ClientHandler
- 处理单个客户端连接
- 读取HTTP请求并发送响应
- 实现Runnable接口

#### 保护方法：
- `onClientConnected(String clientInfo)` - 客户端连接回调
- `onClientDisconnected(String clientInfo)` - 客户端断开连接回调

---

### 4. BaseHttpClient (client/BaseHttpClient.java)

基础HTTP客户端类，提供TCP套接字客户端框架。

#### 主要方法：
- `connect()` - 连接到服务器
- `disconnect()` - 断开与服务器的连接
- `get(String path)` - 发送GET请求
- `post(String path, String body)` - 发送POST请求
- `isConnected()` - 检查是否已连接

#### 属性：
- `host` - 服务器主机名
- `port` - 服务器端口号
- `socket` - 客户端套接字
- `inputStream` - 输入流
- `outputStream` - 输出流
- `connected` - 连接状态

#### 保护方法（回调）：
- `onConnected()` - 连接成功回调
- `onDisconnected()` - 断开连接回调
- `onResponseReceived(HttpResponse response)` - 接收到响应回调
- `onError(Exception e)` - 错误处理回调

---

### 5. HttpClient (client/HttpClient.java)

增强版HTTP客户端，支持异步请求和多线程模拟。

#### 构造函数：
- `HttpClient(String host, int port)` - 使用默认线程池大小创建客户端
- `HttpClient(String host, int port, int poolSize)` - 指定线程池大小创建客户端

#### 主要方法：
- `asyncGet(String path)` - 异步发送GET请求，返回Future
- `asyncPost(String path, String body)` - 异步发送POST请求，返回Future
- `simulateConcurrentUsers(int userCount, String[] paths)` - 模拟多个并发用户
- `shutdown()` - 关闭线程池
- `getPoolSize()` - 获取线程池大小
- `getActiveCount()` - 获取活跃线程数
- `getQueueSize()` - 获取队列大小

#### 内部类：
- `GetTask` - 实现Callable的GET请求任务
- `PostTask` - 实现Callable的POST请求任务

---

## 设计模式

### 继承关系
- `HttpResponse` 继承自基类（如果存在）
- `BaseHttpServer` 是抽象基类，供具体服务器实现继承
- `BaseHttpClient` 是基类，提供基本HTTP客户端功能
- `HttpClient` 继承自 `BaseHttpClient`，扩展异步功能

### 线程池使用
- 服务端使用固定线程池处理并发客户端请求
- 客户端使用线程池支持异步请求和并发用户模拟

### 回调机制
- 服务端提供连接/断开连接回调
- 客户端提供连接、断开、响应接收、错误处理回调

## 使用示例

### 启动服务器
```java
// 创建服务器实例并启动
MyHttpServer server = new MyHttpServer(9090, 10);
server.start();
```

### 发送同步请求
```java
HttpClient client = new HttpClient("localhost", 9090);
client.connect();
HttpResponse response = client.get("/api/test");
client.disconnect();
```

### 发送异步请求
```java
HttpClient client = new HttpClient("localhost", 9090);
client.connect();
Future&lt;HttpResponse&gt; future = client.asyncGet("/api/test");
HttpResponse response = future.get(); // 阻塞直到获取结果
client.disconnect();
```

### 模拟并发用户
```java
HttpClient client = new HttpClient("localhost", 9090);
String[] paths = {"/api/test1", "/api/test2"};
client.simulateConcurrentUsers(5, paths); // 模拟5个并发用户
```