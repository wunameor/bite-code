package web.J2026_02_02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPComputerClient {
    private Socket socket;

    public TCPComputerClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }

    public void start() throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        Scanner inFromConsole = new Scanner(System.in);
        Scanner inFromServer = new Scanner(inputStream);
        while (true) {
            System.out.println("请输入数字1");
            double num1 = Double.parseDouble(inFromConsole.next());
            System.out.println("请输入数字2");
            double num2 = Double.parseDouble(inFromConsole.next());
            System.out.println("请输入运算符");
            String operators = inFromConsole.next();
            Request request = new Request(operators, num1, num2);

            printWriter.print(request.serialized());
            printWriter.flush();

            String responseStr = inFromServer.next();
            Response response = Response.unSerialized(responseStr);
            System.out.println(response.getNum());
        }
    }

    public static void main(String[] args) throws IOException {
        TCPComputerClient client = new TCPComputerClient("127.0.0.1", 9090);
        client.start();
    }
}
