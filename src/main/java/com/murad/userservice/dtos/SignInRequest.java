package com.murad.userservice.dtos;

import lombok.Data;

@Data
public class SignInRequest {

    private String language;

    private String username;

    private String password;
}
