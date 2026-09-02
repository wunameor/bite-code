package web.J2026_03_09.client;

import web.J2026_03_09.model.HttpResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class HttpClient extends BaseHttpClient {
    private final ExecutorService threadPool;
    private final int poolSize;

    public HttpClient(String host, int port) {
        this(host, port, 10);
    }

    public HttpClient(String host, int port, int poolSize) {
        super(host, port);
        this.poolSize = poolSize;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    protected void onConnected() {
        System.out.println("[Client] Connected to " + host + ":" + port);
    }

    @Override
    protected void onDisconnected() {
        System.out.println("[Client] Disconnected");
    }

    @Override
    protected void onResponseReceived(HttpResponse response) {
        System.out.println("[Client] <- " + response.getStatusCode() + " " + response.getStatusMessage());
    }

    @Override
    protected void onError(Exception e) {
        System.err.println("[Client] Error: " + e.getMessage());
    }

    public Future<HttpResponse> asyncGet(String path) {
        return threadPool.submit(new GetTask(path));
    }

    public Future<HttpResponse> asyncPost(String path, String body) {
        return threadPool.submit(new PostTask(path, body));
    }

    public void simulateConcurrentUsers(int userCount, String[] paths) {
        System.out.println("[Client] Simulating " + userCount + " concurrent users...");
        CountDownLatch latch = new CountDownLatch(userCount);

        for (int i = 0; i < userCount; i++) {
            final int userId = i;
            threadPool.submit(() -> {
                try {
                    HttpClient userClient = new HttpClient(host, port, 1);
                    userClient.connect();

                    for (String path : paths) {
                        HttpResponse response = userClient.get(path);
                        System.out.printf("[Client] User %d: GET %s -> %d %s%n",
                            userId, path, response.getStatusCode(), response.getStatusMessage());
                        Thread.sleep(50);
                    }

                    userClient.disconnect();
                } catch (Exception e) {
                    System.err.println("[Client] User " + userId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getPoolSize() {
        return ((java.util.concurrent.ThreadPoolExecutor) threadPool).getPoolSize();
    }

    public int getActiveCount() {
        return ((java.util.concurrent.ThreadPoolExecutor) threadPool).getActiveCount();
    }

    public int getQueueSize() {
        return ((java.util.concurrent.ThreadPoolExecutor) threadPool).getQueue().size();
    }

    private class GetTask implements Callable<HttpResponse> {
        private final String path;

        public GetTask(String path) {
            this.path = path;
        }

        @Override
        public HttpResponse call() throws IOException {
            return get(path);
        }
    }

    private class PostTask implements Callable<HttpResponse> {
        private final String path;
        private final String body;

        public PostTask(String path, String body) {
            this.path = path;
            this.body = body;
        }

        @Override
        public HttpResponse call() throws IOException {
            return post(path, body);
        }
    }
}
