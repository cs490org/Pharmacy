package com.cs490.group4.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("from User")
    List<User> getAllUsers();

    @Query("select firstName from User where userId = :userId")
    String getUserName(Integer userId);

}
