package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderServer {
    private static final int SERVER_PORT = 4444;
    private static final int MAX_EXECUTOR_THREADS = 10;
    private static final MJTOrderRepository ORDER_REPOSITORY = new MJTOrderRepository();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        Thread.currentThread().setName("Order Server Thread");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                ClientOrderHandler clientHandler = new ClientOrderHandler(clientSocket, ORDER_REPOSITORY);
                executor.execute(clientHandler);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error with the server socket", e);
        }
    }

}
