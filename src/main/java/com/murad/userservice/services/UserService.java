package com.murad.userservice.services;

import com.murad.userservice.dtos.PromoDto;
import com.murad.userservice.entities.UsersEntity;
import com.murad.userservice.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final String          USER_NOT_FOUND_MESSAGE = "Пользователь не найден";
    private final        UsersRepository repository;

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Cacheable(value = "usr", key = "#user.username", unless = "#result == null")
    public UsersEntity create(UsersEntity user) {

        return repository.save(user);
    }

    /**
     * Проверяет занят ли логин или нет
     *
     * @return Если занято – то true а если нет – то false
     */
    public boolean isLoginBusy(UsersEntity user) {
        return repository.existsByUsername(user.getUsername());
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    @Cacheable(value = "usr", key = "#username", unless = "#result == null")
    public UsersEntity getByUsername(String username, String password) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public UsersEntity getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

    }

    public UsersEntity getById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

    }

    public UsersEntity getUserByPromoCode(String promo) {
        return repository.findByMyPromoCode(promo).orElse(null);
    }

    @CachePut(value = "usr", key = "#username", unless = "#result == null")
    public UsersEntity editFirstName(String username, String firstName) {
        UsersEntity user = getByUsername(username);
        if (user == null) return null;
        user.setFirstName(firstName);
        return repository.save(user);
    }
    @CachePut(value = "usr", key = "#username", unless = "#result == null")
    public UsersEntity editLastName(String username, String lastName) {
        UsersEntity user = getByUsername(username);
        if (user == null) return null;
        user.setLastName(lastName);
        return repository.save(user);
    }
    @CachePut(value = "usr", key = "#username", unless = "#result == null")
    public UsersEntity editEmail(String username, String email) {
        UsersEntity user = getByUsername(username);
        if (user == null) return null;
        user.setEmail(email);
        return repository.save(user);
    }

    @CachePut(value = "usr", key = "#username", unless = "#result == null")
    public UsersEntity editPassword(String username, String newPassword) {
        UsersEntity user = getByUsername(username);
        if (user == null) return null;
        user.setPassword(newPassword);
        return repository.save(user);
    }

    @CacheEvict(value = "usr", key = "#username")
    public UsersEntity deleteUser(String username) {
        UsersEntity user = getByUsername(username);
        if (user == null) return null;
        user.setActive(false);
        return repository.save(user);
    }

}