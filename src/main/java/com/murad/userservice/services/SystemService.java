package com.murad.userservice.services;

import com.murad.userservice.entities.SystemEntity;
import com.murad.userservice.repositories.SystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final SystemRepository systemRepository;

    public List<SystemEntity> getAll() {
        return systemRepository.findAll();
    }
    @Cacheable(value = "system", key = "#id", unless = "#result == null")
    public SystemEntity getById(Long id) {
        return systemRepository.findById(id).orElse(null);
    }
    @CachePut(value = "system", key = "#result.id", unless = "#result == null")
    public SystemEntity create(SystemEntity system) {
        return systemRepository.save(system);
    }
    @CachePut(value = "system", key = "#id", unless = "#result == null")
    public SystemEntity update(Long id, SystemEntity updatedSystem) {
        return systemRepository.findById(id)
                .map(system -> {
                    system.setName(updatedSystem.getName());
                    system.setSubTypes(updatedSystem.getSubTypes());
                    return systemRepository.save(system);
                })
                .orElseThrow(() -> new RuntimeException("System not found"));
    }
    @CacheEvict(value = "system", key = "#id")
    public void delete(Long id) {
        systemRepository.deleteById(id);
    }
}
