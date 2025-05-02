package com.murad.userservice.controllers;

import com.murad.userservice.dtos.*;
import com.murad.userservice.models.Local;
import com.murad.userservice.models.RedisUser;
import com.murad.userservice.models.RespMessage;
import com.murad.userservice.services.AuthenticationService;
import com.murad.userservice.services.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки запросов аутентификации и управления пользователями.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private static final String                USER_NOT_FOUND = "The user was not found.";
    private final        AuthenticationService authenticationService;
    private final Local        local;
    private final RedisService redisServer;

    /**
     * Регистрация нового пользователя.
     *
     * @param request данные нового пользователя
     * @return токен аутентификации
     */
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody SignUpRequest request, HttpServletRequest req) {
        String ipAddress = getClientIp(req);
        return authenticationService.signUp(request, ipAddress);
    }

    @PostMapping("/sign-up-bot")
    public JwtAuthenticationResponse signUpBot(@RequestBody SignUpRequest request) {
        return authenticationService.signUpBot(request);
    }

    /**
     * Аутентификация пользователя.
     *
     * @param request данные для аутентификации
     * @return токен аутентификации
     */
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request, HttpServletRequest req) {

        String ipAddress = getClientIp(req);
        return authenticationService.signIn(request, ipAddress);
    }

    /**
     * Завершение сеанса аутентификации пользователя.
     *
     * @param token токен аутентификации
     * @return сообщение о результате операции
     */
    @PostMapping("/sign-out")
    public RespMessage signOut(@RequestBody SignOutRequest token) {
        return authenticationService.signOut(token);
    }

    /**
     * Изменение имени пользователя.
     *
     * @param editRequest данные для изменения имени
     * @return токен аутентификации
     */
    @PostMapping("/edit-firstname")
    public JwtAuthenticationResponse editFirstName(@RequestBody EditRequest editRequest) {
        var res = authenticationService.editFirstName(editRequest);
        if (res == null)
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "Dont have firstName.")).error(true).code(1).build();
        return res;
    }

    /**
     * Изменение фамилии пользователя.
     *
     * @param editRequest данные для изменения фамилии
     * @return токен аутентификации
     */
    @PostMapping("/edit-lastname")
    public JwtAuthenticationResponse editLastName(@RequestBody EditRequest editRequest) {
        var res = authenticationService.editLastName(editRequest);
        if (res == null)
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "Dont have lastName.")).error(true).code(1).build();
        return res;
    }

    /**
     * Изменение email пользователя.
     *
     * @param editRequest данные для изменения email
     * @return токен аутентификации
     */
    @PostMapping("/edit-email")
    public JwtAuthenticationResponse editEmail(@RequestBody EditRequest editRequest) {
        var res = authenticationService.editEmail(editRequest);
        if (res == null)
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "Dont have Email.")).error(true).code(1).build();
        return res;
    }


    /**
     * Изменение пароля пользователя.
     *
     * @param editRequest данные для изменения пароля
     * @return токен аутентификации
     */
    @PostMapping("/edit-password")
    public JwtAuthenticationResponse editPassword(@RequestBody EditRequest editRequest) {
        RedisUser redisUser = redisServer.getToken(editRequest.getToken());
        if (isDisconnect(editRequest.getUserId(), redisUser)) {
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), USER_NOT_FOUND)
            ).error(true).build();
        }
        if (editRequest.getNewValue().length() < 4)
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "The password length is less than 4 characters.")).error(true).code(1).build();
        if (!editRequest.getNewValue().equals(editRequest.getConfirmValue()))
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "Confirm password error.")).error(true).code(1).build();
        var res = authenticationService.editPassword(editRequest);
        if (res == null)
            return JwtAuthenticationResponse.builder().message(local.get(editRequest.getLanguage(), "Error.")).error(true).code(1).build();
        return res;
    }

    @GetMapping("/get-promo/{userId}")
    public PromoDto getPromo(@PathVariable long userId) {
        return authenticationService.getPromo(userId);
    }

    private boolean isDisconnect(long userId, RedisUser redisUser) {
        return (redisUser == null || redisUser.getUser() == null || redisUser.getUser().getId() != userId);
    }

    /**
     * Получает реальный IP-адрес клиента, даже если используется прокси или балансировщик нагрузки.
     *
     * @param request HTTP-запрос
     * @return IP-адрес клиента
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // Фоллбэк, если другие заголовки отсутствуют
        }
        // Если клиент за NAT-ом, X-Forwarded-For может содержать список IP через запятую, берем первый
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}