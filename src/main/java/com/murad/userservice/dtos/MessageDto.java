package com.murad.userservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class MessageDto implements Serializable {
    private boolean error;
    private boolean auth;
    private String  message;
    private boolean show;

    public static MessageDto messageOk() {
        return MessageDto.builder()
                .message("")
                .show(false)
                .auth(true)
                .error(false)
                .build();
    }
    public static MessageDto messageOk(String message) {
        return MessageDto.builder()
                .message(message)
                .show(true)
                .auth(true)
                .error(false)
                .build();
    }


    public static MessageDto messageErrorAuthNotShow(String message) {
        return MessageDto.builder()
                .message(message)
                .show(false)
                .auth(false)
                .error(true)
                .build();
    }

    public static MessageDto errorShowMessage(String message) {
        return MessageDto.builder()
                .message(message)
                .show(true)
                .auth(true)
                .error(true)
                .build();
    }
}