package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionBillRepository extends JpaRepository<PrescriptionBill, Integer> {

    @Query("from PrescriptionBill a where a.prescription.patient.id = :patientId")
    List<PrescriptionBill> findAllByPatientId(Integer patientId);

    @Query("""
        select pb from PrescriptionBill pb
        where pb.prescription.patient.id in (
            select pp.patient.id from PatientPharmacy pp where pp.pharmacy.id = :pharmacyId
        )
        """)
    List<PrescriptionBill> findAllByPharmacyId(Integer pharmacyId);

}
