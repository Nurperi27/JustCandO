package maintenance.dev.service.impl;

import maintenance.dev.TransactionHelper;
import maintenance.dev.models.Client;
import maintenance.dev.models.Profile;
import maintenance.dev.repository.ClientRepository;
import maintenance.dev.repository.implRespo.ClientRepoImpl;
import maintenance.dev.service.ProfileService;

public class ProfileServiceImpl implements ProfileService {
    private final TransactionHelper transactionHelper;
    private final ClientRepository clientRepository = new ClientRepoImpl();

    public ProfileServiceImpl(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void updateProfile(Long clientId, String newAddress, String newPhone) {
         transactionHelper.executeInResult(session -> {
            Client client = clientRepository.findById(session, clientId).orElseThrow(() -> new RuntimeException("Клиент не найден"));
            Profile profile = client.getProfile();
            if(profile == null){
                System.out.println("у клиента нет профиля!");
            }
           if(newAddress != null) profile.setAddress(newAddress);
           if(newPhone != null) profile.setPhone(newPhone);
        });
    }
}
