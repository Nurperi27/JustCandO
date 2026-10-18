package maintenance.dev.repository;


import maintenance.dev.models.Coupon;
import org.hibernate.Session;

import java.util.Optional;

public interface CouponRepository {
    void save(Session session, Coupon coupon);
    Optional<Coupon> findById(Session session, Long id);
}
