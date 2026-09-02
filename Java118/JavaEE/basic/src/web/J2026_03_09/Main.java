package web.J2026_03_09;

import web.J2026_03_09.client.HttpClient;
import web.J2026_03_09.model.HttpResponse;
import web.J2026_03_09.server.HttpServer;

import java.io.IOException;
import java.util.concurrent.Future;

public class Main {
    private static final int SERVER_PORT = 9090;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Java HTTP 1.1 Server/Client Demo");
        System.out.println("========================================\n");

        HttpServer server = new HttpServer(SERVER_PORT, 5);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n========== 1. GET Requests ==========\n");
        demonstrateGetRequests();

        System.out.println("\n========== 2. POST Requests ==========\n");
        demonstratePostRequests();

        System.out.println("\n========== 3. Query Parameters ==========\n");
        demonstrateQueryParams();

        System.out.println("\n========== 4. Async Requests ==========\n");
        demonstrateAsyncRequests();

        System.out.println("\n========== 5. Concurrent Users ==========\n");
        demonstrateConcurrentUsers();

        server.stop();
        System.out.println("\n========================================");
        System.out.println("  Demo completed!");
        System.out.println("  You can also test with Postman/ApiFox:");
        System.out.println("  GET  http://localhost:9090/");
        System.out.println("  POST http://localhost:9090/store");
        System.out.println("========================================");
    }

    private static void demonstrateGetRequests() {
        HttpClient client = new HttpClient(SERVER_HOST, SERVER_PORT);
        try {
            client.connect();

            String[] paths = {"/", "/hello", "/time", "/info"};
            for (String path : paths) {
                HttpResponse response = client.get(path);
                System.out.println("  GET " + path + " -> " + response.getStatusCode() + " " + response.getStatusMessage());
                printBodyIndented(response, "    ");
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    private static void demonstratePostRequests() {
        HttpClient client = new HttpClient(SERVER_HOST, SERVER_PORT);
        try {
            client.connect();

            System.out.println("  --- Store a value ---");
            HttpResponse resp1 = client.post("/store",
                "{\"key\":\"name\", \"value\":\"Java Student\"}");
            System.out.println("  POST /store -> " + resp1.getStatusCode() + " " + resp1.getStatusMessage());
            printBodyIndented(resp1, "    ");

            System.out.println("  --- Store another value ---");
            HttpResponse resp2 = client.post("/store",
                "{\"key\":\"course\", \"value\":\"HTTP Protocol\"}");
            System.out.println("  POST /store -> " + resp2.getStatusCode() + " " + resp2.getStatusMessage());
            printBodyIndented(resp2, "    ");

            System.out.println("  --- List all stored values (GET) ---");
            HttpResponse resp3 = client.get("/store");
            System.out.println("  GET /store -> " + resp3.getStatusCode() + " " + resp3.getStatusMessage());
            printBodyIndented(resp3, "    ");

            System.out.println("  --- Get specific value (GET) ---");
            HttpResponse resp4 = client.get("/store/name");
            System.out.println("  GET /store/name -> " + resp4.getStatusCode() + " " + resp4.getStatusMessage());
            printBodyIndented(resp4, "    ");

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    private static void demonstrateQueryParams() {
        HttpClient client = new HttpClient(SERVER_HOST, SERVER_PORT);
        try {
            client.connect();

            HttpResponse response = client.get("/echo?name=Java&lang=CN&version=11");
            System.out.println("  GET /echo?name=Java&lang=CN&version=11 -> " + response.getStatusCode());
            printBodyIndented(response, "    ");
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    private static void demonstrateAsyncRequests() {
        HttpClient client = new HttpClient(SERVER_HOST, SERVER_PORT);
        try {
            client.connect();

            Future<HttpResponse> f1 = client.asyncGet("/hello");
            Future<HttpResponse> f2 = client.asyncGet("/time");
            Future<HttpResponse> f3 = client.asyncGet("/info");

            System.out.println("  Async GET /hello -> " + f1.get().getStatusCode());
            System.out.println("  Async GET /time  -> " + f2.get().getStatusCode());
            System.out.println("  Async GET /info  -> " + f3.get().getStatusCode());

        } catch (Exception e) {
            System.err.println("Async error: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    private static void demonstrateConcurrentUsers() {
        HttpClient client = new HttpClient(SERVER_HOST, SERVER_PORT, 5);

        String[] paths = {"/hello", "/time", "/info"};
        client.simulateConcurrentUsers(5, paths);

        System.out.printf("  Thread pool - Size: %d, Active: %d, Queue: %d%n",
            client.getPoolSize(), client.getActiveCount(), client.getQueueSize());

        client.shutdown();
    }

    private static void printBodyIndented(HttpResponse response, String indent) {
        String body = response.getBody();
        if (body != null && !body.isEmpty()) {
            String[] lines = body.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    System.out.println(indent + line);
                }
            }
        }
    }
}