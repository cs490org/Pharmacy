package com.cs490.group4.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PharmacyDrugInventoryRepository extends JpaRepository<PharmacyDrugInventory, Integer> {

    @Query(value = "select * from pharmacy_drug_inventory a where a.pharmacy_id = :pharmacyId", nativeQuery = true)
    List<PharmacyDrugInventory> findAllByPharmacyId(Integer pharmacyId);

    @Query(value = "select * from pharmacy_drug_inventory a where a.pharmacy_id = :pharmacyId and a.drug_id = :drugId", nativeQuery = true)
    PharmacyDrugInventory findByDrugIdAndPharmacyId(Integer pharmacyId, Integer drugId);

}
