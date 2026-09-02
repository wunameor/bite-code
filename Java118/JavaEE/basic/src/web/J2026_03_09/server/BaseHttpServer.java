package web.J2026_03_09.server;

import web.J2026_03_09.model.HttpRequest;
import web.J2026_03_09.model.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseHttpServer {
    protected final int port;
    protected final int poolSize;
    protected ServerSocket serverSocket;
    protected ExecutorService threadPool;
    protected final AtomicBoolean running = new AtomicBoolean(false);

    public BaseHttpServer(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running.set(true);
        System.out.println("[Server] HTTP Server started on port " + port + " (pool: " + poolSize + ")");

        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(10000);
                threadPool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("[Server] Accept error: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            System.out.println("[Server] HTTP Server stopped");
        } catch (IOException e) {
            System.err.println("[Server] Stop error: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return port;
    }

    protected abstract HttpResponse handleRequest(HttpRequest request, String clientInfo);

    protected void onClientConnected(String clientInfo) {
        System.out.println("[Server] Client connected: " + clientInfo);
    }

    protected void onClientDisconnected(String clientInfo) {
        System.out.println("[Server] Client disconnected: " + clientInfo);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final String clientInfo;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.clientInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }

        @Override
        public void run() {
            onClientConnected(clientInfo);

            try (InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream()) {

                while (running.get() && !socket.isClosed()) {
                    String rawRequest = readHttpRequest(input);
                    if (rawRequest == null) {
                        break;
                    }

                    HttpRequest request = HttpRequest.parse(rawRequest);
                    if (request == null) {
                        HttpResponse badResp = HttpResponse.badRequest("Malformed HTTP request");
                        output.write(badResp.toBytes());
                        output.flush();
                        break;
                    }

                    System.out.println("[Server] " + clientInfo + " -> " + request.getMethod() + " " + request.getPath());

                    HttpResponse response = handleRequest(request, clientInfo);

                    byte[] responseBytes = response.toBytes();
                    output.write(responseBytes);
                    output.flush();

                    System.out.println("[Server] " + clientInfo + " <- " + response.getStatusCode() + " " + response.getStatusMessage());

                    String connectionHeader = request.getHeader("connection");
                    if ("close".equalsIgnoreCase(connectionHeader)) {
                        break;
                    }
                }

            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("[Server] Error with " + clientInfo + ": " + e.getMessage());
                }
            } finally {
                onClientDisconnected(clientInfo);
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        private String readHttpRequest(InputStream input) throws IOException {
            StringBuilder sb = new StringBuilder();
            byte[] single = new byte[1];
            int headerEndCount = 0;

            while (true) {
                int read = input.read(single);
                if (read == -1) {
                    return sb.length() > 0 ? sb.toString() : null;
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

            String headers = sb.toString();

            int contentLength = 0;
            String[] headerLines = headers.split("\r\n");
            for (String line : headerLines) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring(15).trim());
                    break;
                }
            }

            if (contentLength > 0) {
                byte[] bodyBuffer = new byte[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = input.read(bodyBuffer, totalRead, contentLength - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }
                headers += new String(bodyBuffer, 0, totalRead, StandardCharsets.UTF_8);
            }

            return headers;
        }
    }
}
