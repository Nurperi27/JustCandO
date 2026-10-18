package maintenance.dev.service;

import maintenance.dev.models.Profile;

public interface ProfileService{
    void updateProfile(Long clientId, String newAddress, String newPhone);
}
