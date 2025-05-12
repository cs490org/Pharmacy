package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientPharmacyRepository extends JpaRepository<PatientPharmacy, Integer> {
    PatientPharmacy getByPatientId(Integer patientId);
    List<PatientPharmacy> getByPharmacyId(Integer pharmacyId);
    void deleteByPatientId(Integer patientId);
    List<PatientPharmacy> findByPatientIdOrderByCreateTimestampDesc(Integer patientId);
}
