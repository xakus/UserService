package com.murad.userservice.dtos;

import lombok.Data;

@Data
public class SignOutRequest {
    private String language;
    private String token;
}
