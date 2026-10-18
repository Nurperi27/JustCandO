package maintenance.dev.repository.implRespo;

import maintenance.dev.models.Coupon;
import maintenance.dev.repository.CouponRepository;
import org.hibernate.Session;

import java.util.Optional;

public class CouponRepoImpl implements CouponRepository {
    @Override
    public void save(Session session, Coupon coupon) {
        session.persist(coupon);
    }

    @Override
    public Optional<Coupon> findById(Session session, Long id) {
        return Optional.ofNullable(session.find(Coupon.class, id));
    }
}
