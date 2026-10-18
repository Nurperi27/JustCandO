package maintenance.dev.repository.implRespo;

import maintenance.dev.models.Profile;
import maintenance.dev.repository.ProfileRepository;
import org.hibernate.Session;

public class ProfileRepoImpl implements ProfileRepository {
    @Override
    public void save(Session session, Profile profile) {
        session.persist(profile);
    }
}
