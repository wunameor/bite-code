package web.J2026_02_01;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TCPServer {
    private ServerSocket server;

    public TCPServer(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public void start() {
        System.out.println("服务开启");
        ExecutorService service = Executors.newCachedThreadPool();

        while (true) {
            // 建立连接
            try {
                Socket clientSocket = server.accept();
                // 使用线程池 处理客户端的连接
                service.submit(() -> {
                    // 必须在里面 try 不然直接 submit 执行完结束了
                    try (Socket currentSocket = clientSocket) {
                        System.out.printf("[%s:%d] 建立连接\n", currentSocket.getInetAddress().toString(), currentSocket.getPort());

                        processSocket(currentSocket);

                        System.out.printf("[%s:%d] 关闭连接\n", currentSocket.getInetAddress().toString(), currentSocket.getPort());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processSocket(Socket clientSocket) {

        // 1. 获取输入/输出流
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {
            // 分装输入/输出流
            Scanner clientIn = new Scanner(inputStream);
            PrintWriter clientOut = new PrintWriter(outputStream);
            while (true) {
                // 2. 处理输入
                if (!clientIn.hasNext()) {
                    // 没有存在下一个，关闭连接
                    break;
                }
                String data = clientIn.next();
                String result = handleData(data);
                System.out.printf("[%s:%d] data: %s, result: %s\n",
                        clientSocket.getInetAddress().toString(), clientSocket.getPort(), data, result);

                // 3. 把结果返回给客户端
                clientOut.println(result);
                clientOut.flush(); // 把数据从缓存中冲刷到客户端

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String handleData(String data) {
        return data;
    }

    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(9090);
        tcpServer.start();
    }
}
