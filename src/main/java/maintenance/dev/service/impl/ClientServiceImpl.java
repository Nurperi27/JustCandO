package maintenance.dev.service.impl;

import maintenance.dev.TransactionHelper;
import maintenance.dev.models.Client;
import maintenance.dev.models.Coupon;
import maintenance.dev.models.Order;
import maintenance.dev.models.Profile;
import maintenance.dev.repository.ClientRepository;
import maintenance.dev.repository.CouponRepository;
import maintenance.dev.repository.implRespo.ClientRepoImpl;
import maintenance.dev.repository.implRespo.CouponRepoImpl;
import maintenance.dev.service.ClientService;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class ClientServiceImpl implements ClientService {
    private final TransactionHelper transactionHelper;
    private final CouponRepository couponRepository = new CouponRepoImpl();
    private final ClientRepository clientRepository = new ClientRepoImpl();

    public ClientServiceImpl(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public Long addClient(String name, String email, String address, String phone, List<Long> couponIds, Double orderAmount, String orderStatus) {
        return transactionHelper.executeWithResult(session ->
            createClient(session, name, email, address, phone, couponIds, orderAmount, orderStatus));
    }

    private Long createClient(Session session, String name, String email, String address, String phone, List<Long> couponIds, Double orderAmount, String orderStatus) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setRegistrationDate(LocalDateTime.now());
        Profile profile = new Profile();
        profile.setAddress(address);
        profile.setPhone(phone);
        client.setProfile(profile);
        for(Long couponId : couponIds){
            Coupon coupon = couponRepository.findById(session, couponId).orElseThrow(() -> new RuntimeException("coupon not found"));
            client.addCoupon(coupon);
        }
        if(orderAmount != null){ //заказ по желанию
            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setTotalAmount(orderAmount);
            order.setStatus(orderStatus);
            client.addOrders(order); //связывает обе стороны
        }
        clientRepository.save(session, client);
        return client.getId();
    }

    @Override
    public void deleteClient(Long id) {
        transactionHelper.executeInResult(session -> {
            Client client = clientRepository.findById(session, id).orElseThrow(() -> new RuntimeException("client not found"));
            clientRepository.delete(session, client);
        });
    }

    //только печатают и считают заказы (так обращение к getOrders() происходит внутри открытой сессии):
    @Override
    public void printClientsWithOrders(boolean useFetchJoin) {
        transactionHelper.executeInResult(session -> {
            List<Client> clients = useFetchJoin ? clientRepository.finadAllWithOrders(session) : clientRepository.findAll(session);
         for (Client c : clients) {
             System.out.println(c.getName() + ": заказов " + c.getOrders().size());
         }
        });
    }
}
