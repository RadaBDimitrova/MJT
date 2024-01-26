package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MJTOrderRepository implements OrderRepository {
    private final ConcurrentHashMap<Integer, Order> orders;
    private final AtomicInteger orderIdCounter;

    public MJTOrderRepository() {
        this.orders = new ConcurrentHashMap<>();
        this.orderIdCounter = new AtomicInteger(1);
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("Values for request cannot be null");
        }
        int id = orderIdCounter.getAndIncrement();
        Size validatedSize = validateSize(size);
        Color validatedColor = validateColor(color);
        Destination validatedDestination = validateDestination(destination);
        TShirt tShirt = new TShirt(validatedSize, validatedColor);
        Order order = new Order(id, tShirt, validatedDestination);

        if (!isValidSize(size) || !isValidColor(color) || !isValidDestination(destination)) {
            order = new Order(-1, tShirt, validatedDestination);
            orders.put(-1, order);
            return Response.decline(String.format("invalid: %s", getInvalidParams(size, color, destination)));
        }

        orders.put(order.id(), order);
        return Response.create(order.id());
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

    @Override
    public Response getOrderById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        Order order = orders.get(id);
        if (order != null) {
            return Response.ok(List.of(order));
        } else {
            return Response.notFound(id);
        }
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(new ArrayList<>(orders.values()));
    }

    @Override
    public Response getAllSuccessfulOrders() {
        Collection<Order> successfulOrders = orders.values().stream()
                .filter(order -> order.id() > 0)
                .toList();
        return Response.ok(new ArrayList<>(successfulOrders));
    }

    private Size validateSize(String size) {
        try {
            return Size.valueOf(size);
        } catch (IllegalArgumentException e) {
            return Size.UNKNOWN;
        }
    }

    private Color validateColor(String color) {
        try {
            return Color.valueOf(color);
        } catch (IllegalArgumentException e) {
            return Color.UNKNOWN;
        }
    }

    private Destination validateDestination(String destination) {
        try {
            return Destination.valueOf(destination);
        } catch (IllegalArgumentException e) {
            return Destination.UNKNOWN;
        }
    }
}
