package org.example.pionerpixeltest.dao;

import jakarta.validation.constraints.NotEmpty;
import org.example.pionerpixeltest.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhoneIn(@NotEmpty Set<String> phones);
}
