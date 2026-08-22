package web.J2026_02_01;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    private Socket client;

    public TCPClient(String host, int port) throws IOException {
        this.client = new Socket(host, port);
    }

    public void start() {
        try (InputStream inputStream = client.getInputStream();
             OutputStream outputStream = client.getOutputStream()) {
            // 需要移动到外面，不然每一次循环都会创建一次，这样就会丢失数据
            Scanner clientIn = new Scanner(System.in);
            PrintWriter serverOut = new PrintWriter(outputStream);
            Scanner serverIn = new Scanner(inputStream);
            while (true) {
                // 1. 输入数据
                System.out.print("-> ");
                String clientVal = clientIn.next();

                // 2. 发送数据
                // 分装输入/输出流

                serverOut.println(clientVal); // 这个默认是有 \n
                serverOut.flush();

                // 3. 接收数据
                String serverVal = serverIn.next();
                System.out.println(serverVal);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws IOException {
        TCPClient tcpClient = new TCPClient("127.0.0.1", 9090);
        tcpClient.start();
    }
}
