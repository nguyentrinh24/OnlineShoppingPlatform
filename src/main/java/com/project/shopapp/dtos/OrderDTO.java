package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDTO {

    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    private Long userId;

    @JsonProperty("fullname")
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("address")
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("total_money")
    @NotNull(message = "Total money is required")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    @NotBlank(message = "Shipping method is required")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @JsonProperty("cart_items")
    @NotNull(message = "Cart items are required")
    private List<CartItemDTO> cartItems;

}
