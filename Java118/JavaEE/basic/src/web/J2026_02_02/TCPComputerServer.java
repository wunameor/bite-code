package web.J2026_02_02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// computer 是来测试序列化与反序列化
public class TCPComputerServer {
    private ServerSocket serverSocket;

    public TCPComputerServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("服务启动");
        while (true) {
            ExecutorService service = Executors.newCachedThreadPool();
            Socket socket = serverSocket.accept();
            service.submit(() -> {
                try {
                    processClientSocket(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void processClientSocket(Socket socket) throws IOException {
        System.out.printf("[%s:%d] 上线\n", socket.getInetAddress().toString(), socket.getPort());

        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {
            PrintWriter printWriter = new PrintWriter(outputStream);
            Scanner in = new Scanner(inputStream);

            while (true) {
                // 获取输入流
                if (!in.hasNext()) {
                    break;
                }

                String requestStr = in.next();
                Request request = Request.unSerialized(requestStr);
                double result = handleRequest(request);

                System.out.printf("[%s:%d] 计算结果：%f，请求内容：%s\n", socket.getInetAddress().toString(), socket.getPort(), result, requestStr);

                // 返回数据
                printWriter.print(new Response(result).serialized());
                printWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("[%s:%d] 下线\n", socket.getInetAddress().toString(), socket.getPort());
            socket.close();
        }
    }

    private double handleRequest(Request request) {
        return request.calculate();
    }

    public static void main(String[] args) throws IOException {
        TCPComputerServer server = new TCPComputerServer(9090);
        server.start();
    }
}
