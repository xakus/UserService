package com.murad.userservice.services;

import com.murad.userservice.entities.UserLoginLog;
import com.murad.userservice.repositories.UserLoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для логирования входов пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserLoginLogService {

    private final UserLoginLogRepository repository;

    /**
     * Метод для сохранения информации о входе пользователя.
     *
     * @param username  Имя пользователя
     * @param ipAddress IP-адрес пользователя
     */
    public void logUserLogin(long userId, String username, String ipAddress) {
        UserLoginLog log = UserLoginLog.builder()
                .username(username)
                .userId(userId)
                .ipAddress(ipAddress)
                .loginTime(LocalDateTime.now())
                .build();
        repository.save(log);
    }
}
