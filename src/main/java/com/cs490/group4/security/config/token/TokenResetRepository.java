package com.cs490.group4.security.config.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenResetRepository extends JpaRepository<PasswordResetToken, Integer> {

    @Query(value = "select * from password_reset_token where user_id = :userId", nativeQuery = true) // TODO: why does JPA validation fail (set nativeQuery to false to test)?
    Optional<PasswordResetToken> findByUserId(Integer userId);

}