package com.murad.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationTransactionDto {
    private long userId;
    private long promoUserId;
    private String userName;
    private String userRole;
    private String promoUserName;
    private String promoUserRole;
    private String promoCode;    
}