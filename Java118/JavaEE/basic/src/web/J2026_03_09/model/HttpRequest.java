package web.J2026_03_09.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpRequest {
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;

    public HttpRequest() {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.body = "";
        this.httpVersion = "HTTP/1.1";
    }

    public HttpRequest(String method, String path) {
        this();
        this.method = method;
        this.path = path;
    }

    public static HttpRequest parse(String rawRequest) {
        HttpRequest request = new HttpRequest();

        String[] parts = rawRequest.split("\r\n\r\n", 2);
        String headerSection = parts[0];
        if (parts.length > 1) {
            request.body = parts[1];
        }

        String[] lines = headerSection.split("\r\n");
        if (lines.length == 0) {
            return null;
        }

        String[] requestLine = lines[0].split(" ", 3);
        if (requestLine.length < 3) {
            return null;
        }

        request.method = requestLine[0].toUpperCase();
        request.path = requestLine[1];
        request.httpVersion = requestLine[2];

        String fullPath = request.path;
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            request.path = fullPath.substring(0, queryIndex);
            String queryString = fullPath.substring(queryIndex + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int eq = pair.indexOf('=');
                if (eq != -1) {
                    request.queryParams.put(
                        pair.substring(0, eq),
                        pair.substring(eq + 1)
                    );
                } else {
                    request.queryParams.put(pair, "");
                }
            }
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                request.headers.put(key.toLowerCase(), value);
            }
        }

        return request;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
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

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toRawString() {
        StringJoiner sj = new StringJoiner("\r\n");
        sj.add(method + " " + path + " " + httpVersion);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sj.add(capitalizeHeader(entry.getKey()) + ": " + entry.getValue());
        }

        sj.add("");

        if (body != null && !body.isEmpty()) {
            sj.add(body);
        }

        return sj.toString();
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

    @Override
    public String toString() {
        return method + " " + path + " " + httpVersion
            + " | Headers: " + headers.size()
            + " | Body: " + (body == null ? "null" : body.length() + " chars");
    }
}
