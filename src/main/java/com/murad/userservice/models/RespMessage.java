package com.murad.userservice.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespMessage {
    private String msg;
    private boolean error;
}
