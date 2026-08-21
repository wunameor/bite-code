package web.J2026_01_30;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket client;
    private String serveAddr;
    private int servePort;

    public UDPClient(String serveAddr, int servePort) throws SocketException {
        client = new DatagramSocket();
        this.serveAddr = serveAddr;
        this.servePort = servePort;
    }

    public void start() throws IOException {
        Scanner in = new Scanner(System.in);
        while (true) {
            // 1. 设置数据报 由于不保存地址，所以在请求的时候就要设置
            System.out.print("-> ");
            String next = in.next();
            DatagramPacket requestPacket = new DatagramPacket(next.getBytes(),
                    next.getBytes().length,
                    InetAddress.getByName(serveAddr), servePort);

            // 2. 发送
            client.send(requestPacket);

            // 3. 接收
            DatagramPacket responsePacket = new DatagramPacket(
                    new byte[1024], 1024,
                    InetAddress.getByName(serveAddr), servePort);
            client.receive(responsePacket);
            System.out.println(new String(responsePacket.getData(), 0, responsePacket.getLength()));
        }
    }

    public static void main(String[] args) throws IOException {
        UDPClient udpClient = new UDPClient("127.0.0.1", 9090);
        udpClient.start();
    }

}
