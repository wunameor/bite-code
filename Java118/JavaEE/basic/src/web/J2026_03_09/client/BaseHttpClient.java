package web.J2026_03_09.client;

import web.J2026_03_09.model.HttpRequest;
import web.J2026_03_09.model.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public abstract class BaseHttpClient {
    protected final String host;
    protected final int port;
    protected final int timeoutMs;
    protected Socket socket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected boolean connected = false;

    public BaseHttpClient(String host, int port) {
        this(host, port, 5000);
    }

    public BaseHttpClient(String host, int port, int timeoutMs) {
        this.host = host;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }

    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new java.net.InetSocketAddress(host, port), timeoutMs);
        socket.setSoTimeout(timeoutMs);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        connected = true;
        onConnected();
    }

    public void disconnect() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            onError(e);
        } finally {
            connected = false;
            onDisconnected();
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    public HttpResponse sendRequest(HttpRequest request) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to server");
        }

        if (request.getHeader("host") == null) {
            request.setHeader("Host", host + ":" + port);
        }

        byte[] requestBytes = request.toRawString().getBytes(StandardCharsets.UTF_8);
        outputStream.write(requestBytes);
        outputStream.flush();

        HttpResponse response = readHttpResponse();
        onResponseReceived(response);
        return response;
    }

    public HttpResponse get(String path) throws IOException {
        HttpRequest request = new HttpRequest("GET", path);
        request.setHeader("Connection", "keep-alive");
        return sendRequest(request);
    }

    public HttpResponse post(String path, String body) throws IOException {
        HttpRequest request = new HttpRequest("POST", path);
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/json; charset=UTF-8");
        request.setHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        request.setBody(body);
        return sendRequest(request);
    }

    protected HttpResponse readHttpResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] single = new byte[1];
        int headerEndCount = 0;

        while (true) {
            int read = inputStream.read(single);
            if (read == -1) {
                throw new IOException("Connection closed by server");
            }

            sb.append((char) single[0]);

            if (single[0] == '\r') {
                headerEndCount++;
            } else if (single[0] == '\n') {
                headerEndCount++;
            } else {
                headerEndCount = 0;
            }

            if (headerEndCount == 4) {
                break;
            }
        }

        String headerSection = sb.toString();
        String[] lines = headerSection.split("\r\n");

        HttpResponse response = new HttpResponse();

        if (lines.length > 0) {
            String[] statusLine = lines[0].split(" ", 3);
            if (statusLine.length >= 2) {
                response.setStatusCode(Integer.parseInt(statusLine[1]));
                if (statusLine.length >= 3) {
                    response.setStatusMessage(statusLine[2]);
                }
            }
        }

        int contentLength = 0;
        for (int i = 1; i < lines.length; i++) {
            int colonIndex = lines[i].indexOf(':');
            if (colonIndex != -1) {
                String key = lines[i].substring(0, colonIndex).trim();
                String value = lines[i].substring(colonIndex + 1).trim();
                response.setHeader(key, value);

                if (key.equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(value);
                }
            }
        }

        if (contentLength > 0) {
            byte[] bodyBuffer = new byte[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = inputStream.read(bodyBuffer, totalRead, contentLength - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            response.setBody(new String(bodyBuffer, 0, totalRead, StandardCharsets.UTF_8));
        }

        return response;
    }

    protected abstract void onConnected();

    protected abstract void onDisconnected();

    protected abstract void onResponseReceived(HttpResponse response);

    protected abstract void onError(Exception e);
}
