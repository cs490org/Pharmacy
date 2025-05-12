package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    @Query("""
            select p from Prescription p
            where p.patient.id in (
                select pp.patient.id from PatientPharmacy pp where pp.pharmacy.id = :pharmacyId
            )
            """)
    List<Prescription> findAllRxByPharmacyId(Integer pharmacyId);
}