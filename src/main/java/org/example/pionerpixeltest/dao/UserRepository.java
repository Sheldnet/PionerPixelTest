package org.example.pionerpixeltest.dao;

import org.example.pionerpixeltest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmails_EmailIgnoreCase(String email);
    Optional<User> findByPhones_Phone(String phone);

    Optional<User> findByEmails_EmailIgnoreCaseOrPhones_Phone(
            String email, String phone);
}
