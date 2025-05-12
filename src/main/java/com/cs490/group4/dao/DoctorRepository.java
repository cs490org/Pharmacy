package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Query("SELECT d FROM Doctor d JOIN d.user u where u.userId = :userId")
    public Doctor findByUserId(@Param("userId") Integer userId);
}