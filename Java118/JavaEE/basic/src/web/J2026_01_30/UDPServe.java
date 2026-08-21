package web.J2026_01_30;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UDPServe {
    private DatagramSocket serve;

    public UDPServe(int port) throws SocketException {
        this.serve = new DatagramSocket(port);
    }

    public void start() throws IOException {
        while (true) {
            // 1. 监听
            // 创建数据报
            DatagramPacket responsePacket = new DatagramPacket(new byte[1024], 1024);
            serve.receive(responsePacket);

            // 2. 处理请求
            String data = new String(responsePacket.getData(), 0, responsePacket.getLength());


            System.out.println("["+ responsePacket.getAddress() + ":" + responsePacket.getPort()+"] data: " + data);
            String result = handleData(responsePacket);

            // 3. 返回请求
            DatagramPacket requestPacket = new DatagramPacket(
                    result.getBytes(),
                    result.getBytes().length,
                    responsePacket.getSocketAddress()
            );
            serve.send(requestPacket);
        }
    }

    public String handleData(DatagramPacket requestPacket) {
        return new String(requestPacket.getData(), 0, requestPacket.getLength());
    }

    public static void main(String[] args) throws IOException {
        UDPServe udpServe = new UDPServe(9090);

        udpServe.start();
    }
}
