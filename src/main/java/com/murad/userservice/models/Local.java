package com.murad.userservice.models;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Класс для получения локализованных сообщений из Redis.
 */
@Component
@RequiredArgsConstructor
public class Local {
    private static final String REDIS_KEY_PREFIX = "local.";

    private final StringRedisTemplate redisTemplate;

    /**
     * Получение локализованного сообщения для указанной локали и ключа.
     *
     * @param locale язык локали
     * @param key    ключ сообщения
     * @return локализованное сообщение
     */
    public String get(String locale, String key) {
        String str = null;
        try {
            str = (String) redisTemplate.opsForHash().get(REDIS_KEY_PREFIX.toUpperCase()+"::"+key, REDIS_KEY_PREFIX + locale);
        } catch (Exception e) {
            return key;
        }
        if (str == null || str.isEmpty()) return key;
        return str;
    }
    public String getReplace(String locale, String key,String... replace) {
        String str = null;
        try {
            str = (String) redisTemplate.opsForHash().get(REDIS_KEY_PREFIX.toUpperCase()+"::"+key, REDIS_KEY_PREFIX + locale);
            str = replacePlaceholders(str,replace);
        } catch (Exception e) {
            key = replacePlaceholders(key,replace);
            return key;
        }
        if (str == null || str.isEmpty()) return key;
        return str;
    }

    /**
     * Перечисление поддерживаемых языков.
     */
    public enum Lang {
        EN("en"),
        RU("ru"),
        AZ("az"),
        UA("ua");
        private final String code;
        Lang(String code) {
            this.code = code;
        }
        @Override
        public String toString() {
            return code;
        }
    }
    private static String replacePlaceholders(String message, String... rep) {
        // Пробегаемся по всем параметрам rep
        for (int i = 0; i < rep.length; i++) {
            // Составляем строку вида %n1, %n2, и т.д.
            String placeholder = "%n" + (i + 1);
            // Заменяем все вхождения данного плейсхолдера на соответствующее значение из rep
            message = message.replace(placeholder, rep[i]);
        }
        return message;
    }

}
