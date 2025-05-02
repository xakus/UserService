package com.murad.userservice.dtos;

import lombok.Data;

@Data
public class EditRequest {
    private int userId;
    private String language;
    private String token;
    private String oldValue;
    private String newValue;
    private String confirmValue;
}
