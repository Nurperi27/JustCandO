package maintenance.dev.repository;

import maintenance.dev.models.Order;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    void save(Session session, Order order);
    List<Order> findAllOrders(Session session, LocalDateTime date, Double minAmount, String status);
}
