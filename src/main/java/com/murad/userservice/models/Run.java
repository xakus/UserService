package com.murad.userservice.models;

import com.murad.userservice.entities.SystemEntity;
import com.murad.userservice.services.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Run implements ApplicationRunner {
    private final SystemService systemService;

    @Override
    public void run(ApplicationArguments args) {
        List<SystemEntity> systemEntities = systemService.getAll();
        log.error("КЭШ ПРЕГРЕВАЕМ:");
        systemEntities.forEach(systemEntity -> {
            SystemEntity cached = systemService.getById(systemEntity.getId());
        });
    }
}
