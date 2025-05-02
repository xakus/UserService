package com.murad.userservice.models;

import lombok.Data;

@Data
public class RedisUser {
    private String token;
    private User user;
}
