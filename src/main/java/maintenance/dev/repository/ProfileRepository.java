package maintenance.dev.repository;

import maintenance.dev.models.Profile;
import org.hibernate.Session;

public interface ProfileRepository {
    void save(Session session, Profile profile);
}
