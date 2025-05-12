package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    @Query("SELECT p FROM Pharmacy p JOIN p.user u where u.userId = :userId")
    public Pharmacy  findByUserId(Integer userId);

    List<Pharmacy> findByZipCodeBetween(String zipCode1, String zipCode2);
}
