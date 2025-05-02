package com.murad.userservice.services;

import com.murad.userservice.dtos.*;
import com.murad.userservice.entities.RolesEntity;
import com.murad.userservice.entities.UsersEntity;
import com.murad.userservice.models.*;
import com.murad.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Сервис аутентификации пользователей.
 * Отвечает за регистрацию, аутентификацию и управление данными пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private static final String                    ERROR                        = "Error.";
    private static final String                    INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.";
    private static final String                    USER_NOT_FOUND_MESSAGE       = "Пользователь не найден";
    private final        UserService               userService;
    private final        UserLoginLogService       userLoginLogService;
    private final        JwtService                jwtService;
    private final        PasswordEncoder           passwordEncoder;
    private final        Local                     local;
    private final        RedisService              redisService;

    /**
     * Регистрация нового пользователя.
     *
     * @param request данные нового пользователя
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request, String ip) {
        // Создание нового пользователя на основе переданных данных
        UsersEntity promoUser = userService.getUserByPromoCode(request.getPromoCode().toUpperCase());
        if (promoUser == null) request.setPromoCode("");
        var user = UsersEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .promoCode(request.getPromoCode().toUpperCase())
                .createTime(LocalDateTime.now())
                .myPromoCode(promoGeneration(request))
                .active(true)
                .roles(Set.of(RolesEntity.builder().role("USER").build()))
                .build();

        if (userService.isLoginBusy(user)) {
            return JwtAuthenticationResponse.builder().error(true).message(local.getReplace(request.getLanguage(), "The login '%n1' is taken.", request.getUsername())).build();
        }
        // Сохранение пользователя в базе данных
        var ue = userService.create(user);
        log.info("USER REGISTRATION::: {}", ue.toString());
//        if (promoUser == null) {
//            return getJwtAuthenticationResponse(ue);
//        }
        long   promoId   = 0;
        String promoName = "";
        String promoRole = "";
        if (ue.getPromoCode() != null && !ue.getPromoCode().isEmpty()) {
            promoId   = promoUser == null ? 0 : promoUser.getId();
            promoName = promoUser == null ? "" : promoUser.getUsername();
            promoRole = promoUser == null ? "USER" : promoUser.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole();
        }
        UserRegistrationTransactionDto userRegTranDto = UserRegistrationTransactionDto.builder()
                .userId(ue.getId())
                .promoUserId(promoId)
                .userRole(ue.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole())
                .userName(ue.getUsername())
                .promoUserName(promoName)
                .promoCode(ue.getPromoCode().toUpperCase())
                .promoUserRole(promoRole)
                .build();
        if (ue.getRoles().stream().filter(rolesEntity -> rolesEntity.getRole().equals("ROBOT")).toList().isEmpty()) {
            userLoginLogService.logUserLogin(ue.getId(), ue.getUsername(), ip);
        }
        // Генерация и возврат токена аутентификации для нового пользователя
        return getJwtAuthenticationResponse(ue);
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param request данные нового пользователя
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse signUpBot(SignUpRequest request) {
        // Создание нового пользователя на основе переданных данных
        UsersEntity promoUser = userService.getUserByPromoCode(request.getPromoCode().toUpperCase());
        if (promoUser == null) request.setPromoCode("");
        var user = UsersEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .promoCode(request.getPromoCode().toUpperCase())
                .createTime(LocalDateTime.now())
                .myPromoCode(promoGeneration(request))
                .active(true)
                .roles(Set.of(RolesEntity.builder().role("ROBOT").build()))
                .build();

        if (userService.isLoginBusy(user)) {
            return JwtAuthenticationResponse.builder().error(true).message(local.get(request.getLanguage(), "Login") + " \"" + user.getUsername() + "\" " + local.get(request.getLanguage(), "is busy.")).build();
        }
        // Сохранение пользователя в базе данных
        var ue = userService.create(user);
        log.info("ROBOT REGISTRATION::: {}", ue.toString());
        if (promoUser == null) {
            return getJwtAuthenticationResponse(ue);
        }
        long   promoId   = 0;
        String promoName = "";
        String promoRole = "";
        if (ue.getPromoCode() != null && !ue.getPromoCode().isEmpty()) {
            promoId   = promoUser.getId();
            promoName = promoUser.getUsername();
            promoRole = promoUser.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole();
        }
        UserRegistrationTransactionDto userRegTranDto = UserRegistrationTransactionDto.builder()
                .userId(ue.getId())
                .promoUserId(promoId)
                .userRole(ue.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole())
                .userName(ue.getUsername())
                .promoUserName(promoName)
                .promoCode(ue.getPromoCode().toUpperCase())
                .promoUserRole(promoRole)
                .build();
        // Генерация и возврат токена аутентификации для нового пользователя
        return getJwtAuthenticationResponse(ue);
    }

    /**
     * Аутентификация пользователя.
     *
     * @param request данные для аутентификации
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse signIn(SignInRequest request, String ip) {
        try {
            // Получение информации о пользователе
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(request.getUsername());
            // Проверка соответствия введенного пароля хэшированному паролю пользователя
            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                return JwtAuthenticationResponse.builder().error(true).money(BigDecimal.ZERO).message(local.get(request.getLanguage(), INVALID_USERNAME_OR_PASSWORD)).build();
            }
            var user = userService.getByUsername(request.getUsername(), request.getPassword());
            if (user.getRoles().stream().filter(rolesEntity -> rolesEntity.getRole().equals("ROBOT")).toList().isEmpty()) {
                userLoginLogService.logUserLogin(user.getId(), user.getUsername(), ip);
            }
            // Генерация и возврат токена аутентификации для пользователя
            return getJwtAuthenticationResponse(user);
        } catch (UsernameNotFoundException e) {
            return JwtAuthenticationResponse.builder().error(true).money(BigDecimal.ZERO).message(local.get(request.getLanguage(), USER_NOT_FOUND_MESSAGE)).code(2).build();
        } catch (Exception e) {
            log.error("Ошибка при аутентификации пользователя", e);
            // Обработка ошибки аутентификации
            return JwtAuthenticationResponse.builder().error(true).money(BigDecimal.ZERO).message(local.get(request.getLanguage(), INVALID_USERNAME_OR_PASSWORD)).build();
        }
    }

    /**
     * Генерирует токен аутентификации на основе информации о пользователе.
     * Также отправляет токен и данные пользователя в сервисе REST.
     *
     * @param usersEntity информация о пользователе
     * @return токен аутентификации
     */
    private JwtAuthenticationResponse getJwtAuthenticationResponse(UsersEntity usersEntity) {
        // Получение информации о пользователе
        var userDetails = userService
                .userDetailsService()
                .loadUserByUsername(usersEntity.getUsername());
        // Генерация JWT-токена
        String jwt = jwtService.generateToken(userDetails);
        // Создание объекта пользователя для сохранения в сервисе REST
        RedisUser restUser = new RedisUser();
        restUser.setToken(jwt);

        long        friendPromoId   = 0;
        String      friendPromoRole = "";
        UsersEntity promoUser;

        if (usersEntity.getPromoCode() != null && !usersEntity.getPromoCode().trim().isEmpty()) {
            promoUser = userService.getUserByPromoCode(usersEntity.getPromoCode().toUpperCase());
            if (promoUser != null) {
                friendPromoId   = promoUser.getId();
                friendPromoRole = promoUser.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole();
            }
        }
        restUser.setUser(User.builder()
                                 .email(usersEntity.getEmail())
                                 .firstName(usersEntity.getFirstName())
                                 .lastName(usersEntity.getLastName())
                                 .id(usersEntity.getId())
                                 .myPromoCode(usersEntity.getMyPromoCode())
                                 .friendPromoCode(usersEntity.getPromoCode() == null ? "" : usersEntity.getPromoCode())
                                 .friendPromoId(friendPromoId)
                                 .friendPromoRole(friendPromoRole)
                                 .role(usersEntity.getRoles())
                                 .username(usersEntity.getUsername())
                                 .build());
        // Сохранение токена в сервисе REST
        redisService.saveToken(restUser);
        MoneyAndBlueStarDto moneyAndBlueStar = MoneyAndBlueStarDto.builder().money(BigDecimal.ZERO).blueStar(0).build();
        if (moneyAndBlueStar == null) {
            log.error("moneyAndBlueStar IS NULL");
            return JwtAuthenticationResponse.builder().error(true).money(BigDecimal.ZERO).message(local.get("en", ERROR)).build();
        }
        // Возврат токена аутентификации
        return JwtAuthenticationResponse.builder()
                .code(0)
                .error(false)
                .money(moneyAndBlueStar.getMoney())
                .blueStar(moneyAndBlueStar.getBlueStar())
                .paymentSettings(moneyAndBlueStar.getPaymentSettings())
                .message(jwt)
                .user(restUser.getUser())
                .build();
    }

    /**
     * Завершение сеанса аутентификации пользователя.
     *
     * @param token токен аутентификации
     * @return сообщение о результате операции
     */
    public RespMessage signOut(SignOutRequest token) {
        // Удаление токена аутентификации из сервиса REST
        if (redisService.deleteToken(token.getToken())) {
            return RespMessage.builder().error(false).msg("OK").build();
        } else {
            return RespMessage.builder().error(true).msg("ERROR").build();
        }
    }

    /**
     * Изменение имени пользователя.
     *
     * @param editRequest данные для изменения имени
     * @return токен аутентификации
     */
    @SneakyThrows
    public JwtAuthenticationResponse editFirstName(EditRequest editRequest) {
        RedisUser restUser = redisService.getToken(editRequest.getToken());
        if (restUser == null) return null;
        UsersEntity user = userService.editFirstName(restUser.getUser().getUsername(), editRequest.getNewValue());
        if (user == null) return null;
        redisService.deleteToken(editRequest.getToken());
        return getJwtAuthenticationResponse(user);
    }

    /**
     * Изменение фамилии пользователя.
     *
     * @param editRequest данные для изменения фамилии
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse editLastName(EditRequest editRequest) {
        RedisUser restUser = redisService.getToken(editRequest.getToken());
        if (restUser == null) return null;
        UsersEntity user = userService.editLastName(restUser.getUser().getUsername(), editRequest.getNewValue());
        if (user == null) return null;
        redisService.deleteToken(editRequest.getToken());
        return getJwtAuthenticationResponse(user);
    }

    /**
     * Изменение пароля пользователя.
     *
     * @param editRequest данные для изменения пароля
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse editPassword(EditRequest editRequest) {
        RedisUser restUser = redisService.getToken(editRequest.getToken());

        if (restUser == null)
            return JwtAuthenticationResponse.builder().money(BigDecimal.ZERO).error(true).blueStar(0).message(local.get(editRequest.getLanguage(), ERROR)).build();
        UsersEntity usersEntity = userService.getById(editRequest.getUserId());

        if (!passwordEncoder.matches(editRequest.getOldValue(), usersEntity.getPassword())) {
            return JwtAuthenticationResponse.builder().error(true).money(BigDecimal.ZERO).message(local.get(editRequest.getLanguage(), INVALID_USERNAME_OR_PASSWORD)).build();
        }

        UsersEntity user = userService.editPassword(restUser.getUser().getUsername(), passwordEncoder.encode(editRequest.getNewValue()));
        if (user == null)
            return JwtAuthenticationResponse.builder().money(BigDecimal.ZERO).error(true).blueStar(0).message(local.get(editRequest.getLanguage(), ERROR)).build();
        redisService.deleteToken(editRequest.getToken());
        log.info("User ={} changed password oldPassword ={} newPassword ={} ", restUser.getUser().getUsername(), editRequest.getOldValue(), editRequest.getNewValue());
        return getJwtAuthenticationResponse(user);
    }

    /**
     * Изменение email пользователя.
     *
     * @param editRequest данные для изменения email
     * @return токен аутентификации
     */
    public JwtAuthenticationResponse editEmail(EditRequest editRequest) {
        RedisUser restUser = redisService.getToken(editRequest.getToken());
        if (restUser == null) return null;
        UsersEntity user = userService.editEmail(restUser.getUser().getUsername(), editRequest.getNewValue());
        if (user == null) return null;
        redisService.deleteToken(editRequest.getToken());
        return getJwtAuthenticationResponse(user);
    }

    /**
     * Генерация промо кода для нового пользователя.
     *
     * @param request данные нового пользователя
     * @return промо код
     */
    private String promoGeneration(SignUpRequest request) {
        String[]      promoSplit = request.getUsername().trim().toUpperCase().split(" ");
        StringBuilder ret        = new StringBuilder();
        for (String v : promoSplit) {
            ret.append(v);
        }
        return ret.toString();
    }

    public PromoDto getPromo(long userId) {
        if (userId == 0) return PromoDto.builder().build();
        UsersEntity user = userService.getById(userId);
        if (user.getPromoCode() == null || user.getPromoCode().trim().isEmpty()) return PromoDto.builder().build();
        UsersEntity promoUser = userService.getUserByPromoCode(user.getPromoCode().toUpperCase());
        if (promoUser == null) return PromoDto.builder().build();
        return PromoDto.builder()
                .promoId(promoUser.getId())
                .promoUsername(promoUser.getUsername())
                .promoRole(promoUser.getRoles().stream().findFirst().orElse(new RolesEntity()).getRole())

                .build();
    }
}
