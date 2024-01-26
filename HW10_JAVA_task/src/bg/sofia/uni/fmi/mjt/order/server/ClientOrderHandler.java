package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientOrderHandler implements Runnable {
    private static final int LENGTH = 4;
    private static final int THREE = 3;
    private static final int SIX = 6;
    private final Socket socket;
    private final MJTOrderRepository orderRepository;

    public ClientOrderHandler(Socket socket, MJTOrderRepository orderRepository) {
        this.socket = socket;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String input;
            while ((input = in.readLine()) != null) {
                Response response = handleCommand(input);
                out.println(responseToString(response));
            }
        } catch (IOException e) {
            System.err.println("Failed handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed closing client socket: " + e.getMessage());
            }
        }
    }

    private Response handleCommand(String command) {
        String[] tokens = command.split("\\s+");

        if (tokens.length < 1) {
            return Response.decline("Unknown command");
        }

        String action = tokens[0].toLowerCase();

        return switch (action) {
            case "request" -> handleRequestCommand(tokens);
            case "get" -> handleGetCommand(tokens);
            case "disconnect" -> Response.create(0);
            default -> Response.decline("Unknown command");
        };
    }

    private Response handleRequestCommand(String[] tokens) {
        if (tokens.length < LENGTH || !tokens[1].startsWith("size") || !tokens[2].startsWith("color")
                || !tokens[THREE].startsWith("destination")) {
            return Response.decline("Invalid request format");
        }
        int i = 1;
        String size = tokens[i++].substring(SIX - 1);
        String color = tokens[i++].substring(SIX);
        String destination = tokens[i].substring(SIX * 2);

        if (!isValidSize(size) || !isValidColor(color) || !isValidDestination(destination)) {
            return Response.decline(String.format("invalid: %s", getInvalidParams(size, color, destination)));
        }
        return orderRepository.request(size, color, destination);
    }

    private Response handleGetCommand(String[] tokens) {
        if (tokens.length < 2) {
            return Response.decline("Invalid get command format");
        }

        String getAllType = tokens[1].toLowerCase();

        switch (getAllType) {
            case "all":
                return Response.ok(orderRepository.getAllOrders().orders());
            case "all-successful":
                return Response.ok(orderRepository.getAllSuccessfulOrders().orders());
            case "my-order":
                if (tokens.length < LENGTH || !tokens[2].startsWith("id")) {
                    return Response.decline("Invalid command format");
                }
                int orderId = Integer.parseInt(tokens[2].substring(THREE));
                return orderRepository.getOrderById(orderId);
            default:
                return Response.decline("Unknown command");
        }
    }

    private boolean isValidSize(String size) {
        return size.equals("S") || size.equals("M") || size.equals("L") || size.equals("XL");
    }

    private boolean isValidColor(String color) {
        return color.equals("BLACK") || color.equals("WHITE") || color.equals("RED");
    }

    private boolean isValidDestination(String destination) {
        return destination.equals("EUROPE") || destination.equals("NORTH_AMERICA") || destination.equals("AUSTRALIA");
    }

    private String getInvalidParams(String size, String color, String destination) {
        StringBuilder invalidParams = new StringBuilder();
        if (!isValidSize(size)) {
            invalidParams.append("size,");
        }
        if (!isValidColor(color)) {
            invalidParams.append("color,");
        }
        if (!isValidDestination(destination)) {
            invalidParams.append("destination,");
        }
        return invalidParams.toString().replaceAll(",$", "");
    }

    private String responseToString(Response response) {
        return String.format("{'status':'%s', 'additionalInfo':'%s', 'orders': %s}",
                response.status(), response.additionalInfo(), response.orders());
    }
}
