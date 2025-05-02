package com.murad.userservice.models;

import com.murad.userservice.dtos.MessageDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PaymentSettingsDto {
    private MessageDto message;

    private String     allMessage;
    private boolean    active;
    private String     payMessage;
    private String     withdrawMessage;
    private String     moneySymbol;
    private BigDecimal payMinAmount;
    private BigDecimal payMaxAmount;
    private BigDecimal withdrawMinAmount;
    private BigDecimal withdrawMaxAmount;
    private boolean    showMoneySymbol;
    private boolean    payActive;
    private boolean    withdrawActive;
    private boolean    useFakePaymentSystem;

}
