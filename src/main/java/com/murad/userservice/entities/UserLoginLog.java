package com.murad.userservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность для хранения информации о входах пользователей в систему.
 */
@Entity
@Table(name = "user_login_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginLog {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя, который вошел в систему.
     */
    private String username;

    /**
     * Имя пользователя, который вошел в систему.
     */
    private long userId;

    /**
     * IP-адрес пользователя при входе.
     */
    private String ipAddress;

    /**
     * Дата и время входа пользователя.
     */
    private LocalDateTime loginTime;

}
