package maintenance.dev.service.impl;

import maintenance.dev.TransactionHelper;
import maintenance.dev.models.Coupon;
import maintenance.dev.repository.CouponRepository;
import maintenance.dev.repository.implRespo.CouponRepoImpl;
import maintenance.dev.service.CouponService;

import java.time.LocalDateTime;
import java.util.Scanner;

public class CouponServiceImpl implements CouponService {
    private final TransactionHelper  transactionHelper;
    private final CouponRepository couponRepository = new CouponRepoImpl();

    public CouponServiceImpl(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void updateCoupon(Long couponId, String newCode, Float newDiscount, LocalDateTime newExpirationDate) {
        transactionHelper.executeInResult(session -> {
            Coupon coupon = couponRepository.findById(session, couponId).orElseThrow(() -> new RuntimeException("Coupon not found"));
           LocalDateTime now = LocalDateTime.now();
           boolean expiredDate = !coupon.getExpirationDate().isAfter(now);
           if(expiredDate && (newExpirationDate == null || !newExpirationDate.isAfter(now) )) {
               throw new RuntimeException("Срок купона истёк, укажите новую дату в будущем");
           }
            if(newCode != null) coupon.setCode(newCode);
            if(newDiscount != null) coupon.setDiscount(newDiscount);
            if(newExpirationDate != null) coupon.setExpirationDate(newExpirationDate);
        });
    }

    @Override
    public boolean isExpiredDate(Long couponId) {
        return transactionHelper.executeWithResult(session -> {
            Coupon coupon = couponRepository.findById(session, couponId).orElseThrow(() -> new RuntimeException("Coupon not found"));
            return !coupon.getExpirationDate().isAfter(LocalDateTime.now()); //!isAfter(now) означает «срок равен текущему моменту или раньше него»
        });
    }
}
