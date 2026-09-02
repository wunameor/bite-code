package web.J2026_03_09.server;

import web.J2026_03_09.model.HttpRequest;
import web.J2026_03_09.model.HttpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HttpServer extends BaseHttpServer {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, String> store = new HashMap<>();

    public HttpServer(int port, int poolSize) {
        super(port, poolSize);
        // 初始化默认内容
        store.put("welcome", "Welcome to Java HTTP Server!");
        store.put("author", "Java Student");
        store.put("course", "HTTP Protocol");
        store.put("year", "2026");
    }

    @Override
    protected HttpResponse handleRequest(HttpRequest request, String clientInfo) {
        String method = request.getMethod();
        String path = request.getPath();

        if ("GET".equals(method)) {
            return handleGet(request);
        } else if ("POST".equals(method)) {
            return handlePost(request);
        } else {
            return HttpResponse.methodNotAllowed();
        }
    }

    private HttpResponse handleGet(HttpRequest request) {
        String path = request.getPath();

        switch (path) {
            case "/":
                return HttpResponse.ok("Welcome to Java HTTP Server!\n"
                    + "Available endpoints:\n"
                    + "  GET  /           - This page\n"
                    + "  GET  /hello      - Hello world\n"
                    + "  GET  /time       - Current server time\n"
                    + "  GET  /info       - Server information\n"
                    + "  GET  /echo       - Echo query params (use ?key=value)\n"
                    + "  GET  /store/:key - Get value from store\n"
                    + "  POST /store      - Store a key-value pair (JSON body)\n"
                    + "  GET  /store      - List all stored key-value pairs\n");

            case "/hello":
                return HttpResponse.ok("Hello, World!\n");

            case "/time":
                return HttpResponse.ok("Server time: " + LocalDateTime.now().format(TIME_FMT) + "\n");

            case "/info":
                return HttpResponse.ok(
                    "Server: JavaHttpServer/1.0\n"
                    + "Protocol: HTTP/1.1\n"
                    + "Port: " + port + "\n"
                    + "Thread pool: " + poolSize + "\n"
                    + "Supported methods: GET, POST\n"
                );

            case "/echo":
                StringBuilder echoBody = new StringBuilder("Echo:\n");
                for (Map.Entry<String, String> entry : request.getQueryParams().entrySet()) {
                    echoBody.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
                }
                if (request.getQueryParams().isEmpty()) {
                    echoBody.append("  (no query parameters, use ?key=value)\n");
                }
                return HttpResponse.ok(echoBody.toString());

            default:
                if (path.startsWith("/store/")) {
                    String key = path.substring("/store/".length());
                    String value = store.get(key);
                    if (value != null) {
                        return HttpResponse.okJson("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}\n");
                    } else {
                        return HttpResponse.notFound();
                    }
                }
                if ("/store".equals(path)) {
                    StringBuilder sb = new StringBuilder("{\n");
                    boolean first = true;
                    for (Map.Entry<String, String> entry : store.entrySet()) {
                        if (!first) sb.append(",\n");
                        sb.append("  \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
                        first = false;
                    }
                    sb.append("\n}\n");
                    return HttpResponse.okJson(sb.toString());
                }
                return HttpResponse.notFound();
        }
    }

    private HttpResponse handlePost(HttpRequest request) {
        String path = request.getPath();
        String body = request.getBody();

        if ("/store".equals(path)) {
            String key = extractJsonField(body, "key");
            String value = extractJsonField(body, "value");

            if (key == null || key.isEmpty()) {
                return HttpResponse.badRequest("Missing 'key' field in body");
            }

            store.put(key, value != null ? value : "");
            return HttpResponse.okJson(
                "{\"status\":\"stored\",\"key\":\"" + key + "\",\"value\":\"" + (value != null ? value : "") + "\"}\n"
            );
        }

        if ("/echo".equals(path)) {
            return HttpResponse.ok("POST body:\n" + body + "\n");
        }

        return HttpResponse.notFound();
    }

    private String extractJsonField(String json, String field) {
        if (json == null || json.isEmpty()) return null;

        String keyPattern = "\"" + field + "\"";
        int keyIndex = json.indexOf(keyPattern);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(':', keyIndex + keyPattern.length());
        if (colonIndex == -1) return null;

        int quoteStart = json.indexOf('"', colonIndex + 1);
        if (quoteStart == -1) return null;

        int quoteEnd = json.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) return null;

        return json.substring(quoteStart + 1, quoteEnd);
    }

    @Override
    protected void onClientConnected(String clientInfo) {
        System.out.println("[Server] + Client connected: " + clientInfo);
    }

    @Override
    protected void onClientDisconnected(String clientInfo) {
        System.out.println("[Server] - Client disconnected: " + clientInfo);
    }
}
