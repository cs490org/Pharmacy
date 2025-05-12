package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DrugRepository extends JpaRepository<Drug, Integer> {
    @Query("""
        SELECT d FROM Drug d
        WHERE d.id NOT IN (
            SELECT inv.drug.id
            FROM PharmacyDrugInventory inv
            WHERE inv.pharmacy.id = :pharmacyId
        )
    """)
    List<Drug> findDrugsNotInPharmacy(Integer pharmacyId);
}
