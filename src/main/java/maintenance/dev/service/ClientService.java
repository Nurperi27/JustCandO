package maintenance.dev.service;

import maintenance.dev.models.Client;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientService {
    Long addClient(String name, String email, String address, String phone, List<Long> couponIds, Double orderAmount, String orderStatus);
    void deleteClient(Long id);

    /// for case-8
    //только печатают и считают заказы (так обращение к getOrders() происходит внутри открытой сессии):
    void printClientsWithOrders(boolean useFetchJoin);
}
