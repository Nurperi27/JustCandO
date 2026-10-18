package maintenance.dev.service;

import maintenance.dev.models.Coupon;

import java.time.LocalDateTime;

public interface CouponService {
    void updateCoupon(Long couponId, String newCode, Float newDiscount, LocalDateTime newExpirationDate);
    public boolean isExpiredDate(Long couponId);
}
