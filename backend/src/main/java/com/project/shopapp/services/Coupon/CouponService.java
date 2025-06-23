package com.project.shopapp.services.Coupon;

import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.repositories.CouponConditionRepositories;
import com.project.shopapp.repositories.CouponRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService  implements ICouponService{
    private final CouponRepositories couponRepositories;
    private final CouponConditionRepositories couponConditionRepositories;

    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) {
        Coupon coupon = couponRepositories.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon, totalAmount);
        double finalAmount = totalAmount - discount;
        return finalAmount;
    }

    private double calculateDiscount(Coupon coupon, double totalAmount) {
        List<CouponCondition> conditions = couponConditionRepositories
                .findByCouponId(coupon.getId().longValue());
        double discount = 0.0;
        double updatedTotalAmount = totalAmount;
        for (CouponCondition condition : conditions) {
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();

            double percentDiscount = Double.valueOf(
                    String.valueOf(condition.getDiscountAmount()));

            if (attribute.equals("minimum_amount")) {
                if (operator.equals(">") && updatedTotalAmount > Double.parseDouble(value)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
            } else if ("applicable_date".equals(attribute)) {
                // chấp nhận cả "2025-5-25" hoặc "2025-05-25"
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-M-d");
                LocalDate targetDate = LocalDate.parse(value.trim(), fmt);
                LocalDate today = LocalDate.now();

                // nếu đúng ngày thì áp dụng giảm giá
                if (today.isEqual(targetDate)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
            }

            updatedTotalAmount = updatedTotalAmount - discount;
        }
        return discount;
    }
}
