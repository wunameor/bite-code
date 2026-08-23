package web.J2026_02_02;

import web.J2026_02_01.TCPServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TCPTranslateServer extends TCPServer {
    private Map<String, String> dict = new HashMap<>();

    public TCPTranslateServer(int port) throws IOException {
        super(port);
        dict.put("hello", "你好");
        dict.put("test", "测试");
        dict.put("你好", "hi");
        dict.put("stir", "搅拌");
        dict.put("我的", "my");
    }

    @Override
    public String handleData(String data) {
        return dict.getOrDefault(data, "[未找到相关翻译]");
    }

    public static void main(String[] args) throws IOException {
        TCPTranslateServer server = new TCPTranslateServer(9090);
        server.start();
    }
}
