package maintenance.dev.repository.implRespo;

import maintenance.dev.models.Order;
import maintenance.dev.repository.OrderRepository;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRepoImpl implements OrderRepository {
    @Override
    public void save(Session session, Order order) {
        session.persist(order);
    }

    @Override
    public List<Order> findAllOrders(Session session, LocalDateTime date, Double minAmount, String status) {
        StringBuilder jpql = new StringBuilder("select o from Order o join fetch o.client where 1=1"); //where 1=1 позволяет дописывать and ... для каждого заполненного фильтра, не думая о первом условии
                                                //join fetch o.client сразу подтягивает клиента каждого заказа, чтобы при выводе имени клиента не было N+1
        if(date != null) {
            jpql.append(" and o.orderDate >= :from and o.orderDate < :to");
        }
        if (minAmount != null) jpql.append(" and o.totalAmount >= :amount");
        if (status != null) jpql.append(" and lower(o.status) = :status");
        jpql.append(" order by o.orderDate desc");
        var query = session.createQuery(jpql.toString(), Order.class);
        if(date != null) {
            query.setParameter("from", date.toLocalDate().atStartOfDay());
            query.setParameter("to", date.plusDays(1).toLocalDate().atStartOfDay());
        }
        if (minAmount != null) query.setParameter("amount", minAmount);
        if (status != null)    query.setParameter("status", status.toLowerCase());
        return query.getResultList();
    }
}
