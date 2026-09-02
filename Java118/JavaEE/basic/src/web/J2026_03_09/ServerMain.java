package web.J2026_03_09;

import web.J2026_03_09.server.HttpServer;

import java.io.IOException;

public class ServerMain {
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Java HTTP 1.1 Server");
        System.out.println("  Listening on port: " + SERVER_PORT);
        System.out.println("  Test with Postman/ApiFox:");
        System.out.println("  GET  http://localhost:" + SERVER_PORT + "/");
        System.out.println("  POST http://localhost:" + SERVER_PORT + "/store");
        System.out.println("========================================");

        HttpServer server = new HttpServer(SERVER_PORT, 10);

        try {
            server.start(); // This will keep the server running
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}