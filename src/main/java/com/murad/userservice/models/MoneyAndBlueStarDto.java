package com.murad.userservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyAndBlueStarDto implements Serializable {
    private BigDecimal money;
    private long blueStar;
    private PaymentSettingsDto paymentSettings;
}
