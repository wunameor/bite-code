package web.J2026_03_09.model;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.headers = new HashMap<>();
        this.body = "";
        this.statusCode = 200;
        this.statusMessage = "OK";
    }

    public static HttpResponse ok(String body) {
        HttpResponse resp = new HttpResponse();
        resp.statusCode = 200;
        resp.statusMessage = "OK";
        resp.body = body;
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return resp;
    }

    public static HttpResponse okJson(String jsonBody) {
        HttpResponse resp = ok(jsonBody);
        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        return resp;
    }

    public static HttpResponse notFound() {
        HttpResponse resp = new HttpResponse();
        resp.statusCode = 404;
        resp.statusMessage = "Not Found";
        resp.body = "404 Not Found";
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return resp;
    }

    public static HttpResponse badRequest(String message) {
        HttpResponse resp = new HttpResponse();
        resp.statusCode = 400;
        resp.statusMessage = "Bad Request";
        resp.body = "400 Bad Request: " + message;
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return resp;
    }

    public static HttpResponse methodNotAllowed() {
        HttpResponse resp = new HttpResponse();
        resp.statusCode = 405;
        resp.statusMessage = "Method Not Allowed";
        resp.body = "405 Method Not Allowed";
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return resp;
    }

    public static HttpResponse serverError(String message) {
        HttpResponse resp = new HttpResponse();
        resp.statusCode = 500;
        resp.statusMessage = "Internal Server Error";
        resp.body = "500 Internal Server Error: " + message;
        resp.setHeader("Content-Type", "text/plain; charset=UTF-8");
        return resp;
    }

    public byte[] toBytes() {
        StringJoiner sj = new StringJoiner("\r\n");
        sj.add("HTTP/1.1 " + statusCode + " " + statusMessage);

        byte[] bodyBytes = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];

        if (!headers.containsKey("content-length")) {
            headers.put("content-length", String.valueOf(bodyBytes.length));
        }
        if (!headers.containsKey("connection")) {
            headers.put("connection", "keep-alive");
        }
        if (!headers.containsKey("server")) {
            headers.put("server", "JavaHttpServer/1.0");
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sj.add(capitalizeHeader(entry.getKey()) + ": " + entry.getValue());
        }

        sj.add("");
        sj.add("");

        String headerPart = sj.toString();
        byte[] headerBytes = headerPart.getBytes(StandardCharsets.UTF_8);

        byte[] result = new byte[headerBytes.length + bodyBytes.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, result, headerBytes.length, bodyBytes.length);
        return result;
    }

    private String capitalizeHeader(String name) {
        String[] parts = name.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append("-");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + statusCode + " " + statusMessage
            + " | Headers: " + headers.size()
            + " | Body: " + (body == null ? "null" : body.length() + " chars");
    }
}
