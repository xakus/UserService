package com.murad.userservice.dtos;

import lombok.Data;

@Data
public class SignUpRequest {

    private String language;

    private String username;

    private String email;

    private String password;

    private String promoCode;

    private String firstName;

    private String lastName;
}