package com.murad.userservice.dtos;

import com.murad.userservice.models.PaymentSettingsDto;
import com.murad.userservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private boolean    error;
    private int        code;
    private String     message;
    private User       user;
    private BigDecimal money;
    private PaymentSettingsDto paymentSettings;
    private long       blueStar;
}