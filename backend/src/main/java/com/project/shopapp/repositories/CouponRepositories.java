package com.project.shopapp.repositories;

import com.project.shopapp.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepositories extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String couponId);
}
