package web.J2026_01_30;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;

public class TranslateServe extends UDPServe{

    private HashMap<String, String> translateMap = new HashMap<>();

    public TranslateServe(int port) throws SocketException {
        super(port);
        translateMap.put("hello", "你好");
        translateMap.put("test", "测试");
        translateMap.put("你好", "hi");
        translateMap.put("stir", "搅拌");
        translateMap.put("我的", "my");
    }

    @Override
    public String handleData(DatagramPacket requestPacket) {
        String key = new String(requestPacket.getData(), 0, requestPacket.getLength());
        String result = translateMap.get(key);
        return result == null ? "[未找到相关翻译]" : result;
    }

    public static void main(String[] args) throws IOException {
        TranslateServe serve = new TranslateServe(9090);
        serve.start();
    }
}
