package maintenance.dev.repository;

import maintenance.dev.models.Client;
import maintenance.dev.models.Coupon;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    void save(Session session, Client client);
    Optional<Client> findById(Session session, Long id);
    void delete(Session session, Client client);

    ///для case-8
    //N+1: 1 запрос на клиентов, затем по запросу на заказы каждого клиента
    List<Client> findAll(Session session);

    // 1 запрос с join
    List<Client> finadAllWithOrders(Session session);
}
