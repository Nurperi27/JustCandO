package maintenance.dev.repository.implRespo;

import maintenance.dev.models.Client;
import maintenance.dev.models.Coupon;
import maintenance.dev.repository.ClientRepository;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class ClientRepoImpl implements ClientRepository {
    @Override
    public void save(Session session, Client client) {
        session.persist(client);
    }

    @Override
    public Optional<Client> findById(Session session, Long id) {
        return Optional.ofNullable(session.find(Client.class, id));
    }

    @Override
    public void delete(Session session, Client client) {
        session.remove(client);
    }

    //N+1: 1 запрос на клиентов, затем по запросу на заказы каждого клиента
    @Override
    public List<Client> findAll(Session session) {
        return session.createQuery("select c from Client c", Client.class).getResultList();
    }

    // 1 запрос с join
    @Override
    public List<Client> finadAllWithOrders(Session session) {
        return session.createQuery("select distinct c from Client c left join fetch c.orders", Client.class).getResultList();
    }
}
