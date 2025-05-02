package com.murad.userservice.services;

import com.google.gson.Gson;
import com.murad.userservice.models.RedisUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    final         String              USER_REF = "Users::";
    private final Gson                gson;
    private final StringRedisTemplate redisTemplate;

    public void saveToken(RedisUser restUser) {
        if (restUser == null || restUser.getToken() == null) {
            log.error("RedisUser OR TOKEN IS EMPTY OR NULL!!!");
            return;
        }
        checkOnCollision(restUser);
        String json = gson.toJson(restUser);
        redisTemplate.opsForValue().set(USER_REF + restUser.getToken(), json, 86400, TimeUnit.SECONDS);
    }

    public RedisUser getToken(String token) {
        String json = redisTemplate.opsForValue().get(USER_REF + token);
        if (json == null || json.isEmpty()) return null;
        try {
            return gson.fromJson(json, RedisUser.class);
        } catch (Exception e) {
            log.error("Error getToken: ", e);
            return null;
        }
    }

    private List<RedisUser> getTokens() {
        // Получаем все ключи
        Set<String> keys = redisTemplate.keys(USER_REF + "*");

        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<RedisUser> users = new ArrayList<>();
        try {
            for (String key : keys) {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    RedisUser user = gson.fromJson(json, RedisUser.class);
                    users.add(user);
                }
            }
        } catch (Exception e) {
            log.error("Error getTokens: ", e);
        }
        return users;
    }

    private void checkOnCollision(RedisUser restUser) {
        List<RedisUser> users = getTokens();
        if (users.isEmpty()) {
            log.info("В кеше нет токенов.");
            return;
        }
        List<String> removeTokens = new ArrayList<>();
        for (RedisUser user : users) {
            if (user.getUser().getId() == restUser.getUser().getId()) {
                removeTokens.add(user.getToken());
            }
        }
        if (removeTokens.isEmpty()) {
            return;
        }
        removeTokens.forEach(this::deleteToken);
        removeTokens.clear();
    }

    public boolean deleteToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.delete(USER_REF + token));
    }
}
