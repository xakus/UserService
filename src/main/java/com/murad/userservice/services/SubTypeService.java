package com.murad.userservice.services;

import com.murad.userservice.entities.SubTypeEntity;
import com.murad.userservice.repositories.SubTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubTypeService {

    private final SubTypeRepository subTypeRepository;

    public List<SubTypeEntity> getAll() {
        return subTypeRepository.findAll();
    }

    public Optional<SubTypeEntity> getById(Long id) {
        return subTypeRepository.findById(id);
    }

    public SubTypeEntity create(SubTypeEntity subType) {
        return subTypeRepository.save(subType);
    }

    public SubTypeEntity update(Long id, SubTypeEntity updatedSubType) {
        return subTypeRepository.findById(id)
                .map(subType -> {
                    subType.setName(updatedSubType.getName());
                    subType.setSystem(updatedSubType.getSystem());
                    return subTypeRepository.save(subType);
                })
                .orElseThrow(() -> new RuntimeException("SubType not found"));
    }

    public void delete(Long id) {
        subTypeRepository.deleteById(id);
    }
}
