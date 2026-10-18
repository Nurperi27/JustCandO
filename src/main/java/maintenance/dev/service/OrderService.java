package maintenance.dev.service;

import maintenance.dev.models.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    void createOrder(Long idClient, LocalDateTime orderDate, Double totalAmount, String status);
    List<String> searchOrders(LocalDateTime date, Double minAmount, String status);
}
