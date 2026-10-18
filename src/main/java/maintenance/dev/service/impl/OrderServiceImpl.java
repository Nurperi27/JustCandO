package maintenance.dev.service.impl;

import maintenance.dev.TransactionHelper;
import maintenance.dev.models.Client;
import maintenance.dev.models.Order;
import maintenance.dev.repository.ClientRepository;
import maintenance.dev.repository.OrderRepository;
import maintenance.dev.repository.implRespo.ClientRepoImpl;
import maintenance.dev.repository.implRespo.OrderRepoImpl;
import maintenance.dev.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final TransactionHelper transactionHelper;
    private final ClientRepository clientRepository = new ClientRepoImpl();
    private final OrderRepository orderRepository = new OrderRepoImpl();

    public OrderServiceImpl(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void createOrder(Long idClient, LocalDateTime orderDate, Double totalAmount, String status) {
        transactionHelper.executeWithResult(session -> {
            Client client = clientRepository.findById(session, idClient).orElseThrow(() -> new RuntimeException("client not found"));
            if(client.getOrders() != null) {
                System.out.println("Order already exists");
            }
            Order order = new Order();
            order.setOrderDate(orderDate);
            order.setTotalAmount(totalAmount);
            order.setStatus(status);
            order.setClientId(client);
            orderRepository.save(session, order);
            return order.getId();
        });
    }

    @Override
    public List<String> searchOrders(LocalDateTime date, Double minAmount, String status) {
        return transactionHelper.executeWithResult(session -> orderRepository.findAllOrders(session, date, minAmount, status).stream()
                .map(o -> String.format("Заказ #%d | %s | %.2f | %s | клиент: %s",
                        o.getId(), o.getOrderDate().toLocalDate(), o.getTotalAmount(), o.getStatus(), o.getClientId().getName())).toList());
    }
}
