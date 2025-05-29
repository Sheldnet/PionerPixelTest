package org.example.pionerpixeltest.dao;

import jakarta.validation.constraints.NotEmpty;
import org.example.pionerpixeltest.domain.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<EmailData> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseIn(@NotEmpty Set<String> emails);
}
