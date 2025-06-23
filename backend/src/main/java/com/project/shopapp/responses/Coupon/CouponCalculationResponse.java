package com.project.shopapp.responses.Coupon;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CouponCalculationResponse {

        @JsonProperty("result")
        private Double result;

        //errorCode
        @JsonProperty("errorMessage")
        private String errorMessage;
}
