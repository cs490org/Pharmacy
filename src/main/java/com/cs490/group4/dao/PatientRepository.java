package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    @Query("SELECT p FROM Patient p JOIN p.user u where u.userId = :userId")
    public Patient findByUserId(@Param("userId") Integer userId);
}