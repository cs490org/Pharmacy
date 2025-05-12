package com.cs490.group4.service;

import com.cs490.group4.dao.*;
import com.cs490.group4.dto.InventoryDTO;
import com.cs490.group4.dto.PharmacyCreateDTO;
import com.cs490.group4.dto.PrescriptionBillDTO;
import com.cs490.group4.security.User;
import com.cs490.group4.security.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final PharmacyDrugInventoryRepository pharmacyDrugInventoryRepository;
    private final PrescriptionBillRepository prescriptionBillRepository;
    private final UserRepository userRepository;

    public List<Pharmacy> getPharmacies() {
        return pharmacyRepository.findAll();
    }

    public List<Pharmacy> findNearbyPharmacies(String zipCode) {

        int baseZip = Integer.parseInt(zipCode);
        int range = 5;

        int minZip = baseZip - range;
        int maxZip = baseZip + range;

        return pharmacyRepository.findByZipCodeBetween(String.format("%05d", minZip), String.format("%05d", maxZip));
    }


    public Pharmacy getPharmacyByUserId(Integer userId) {
        Pharmacy pharmacy = pharmacyRepository.findByUserId(userId);
        if (pharmacy == null) {
            throw new EntityNotFoundException("pharmacist not defined for user " + userId);
        }
        return pharmacy;
    }


    @Transactional
    public Pharmacy createPharmacy(PharmacyCreateDTO pharmacyCreateDTO) {
        User user = userRepository.findById(pharmacyCreateDTO.getUserId()).orElseThrow(
                ()-> new EntityNotFoundException("User Not Found"));

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setUser(user);
        pharmacy.setName(pharmacyCreateDTO.getName());
        pharmacy.setZipCode(pharmacyCreateDTO.getZipCode());
        pharmacy.setPhone(pharmacyCreateDTO.getPhone());
        pharmacy.setAddress(pharmacyCreateDTO.getAddress());

        return pharmacyRepository.save(pharmacy);
    }

    public boolean isEmpty() {
        return pharmacyRepository.count() == 0;
    }

    public List<PharmacyDrugInventory> getDrugsByPharmacy(Integer pharmacyId){
        return pharmacyDrugInventoryRepository.findAllByPharmacyId(pharmacyId);
    }

    public void updateDrugInventory(InventoryDTO dto) {
        PharmacyDrugInventory toSave = PharmacyDrugInventory.builder()
                .drug(Drug.builder().id(dto.getDrugId()).build())
                .pharmacy(Pharmacy.builder().id(dto.getPharmacyId()).build())
                .inventory(dto.getQuantity())
                .createTimestamp(LocalDateTime.now())
                .updateTimestamp(LocalDateTime.now())
                .dispensed(dto.getDispensed())
                .build();

        PharmacyDrugInventory pharmacyDrugInventory = pharmacyDrugInventoryRepository.findByDrugIdAndPharmacyId(dto.getPharmacyId(), dto.getDrugId());
        if(pharmacyDrugInventory != null){
            toSave.setId(pharmacyDrugInventory.getId());
        }

        pharmacyDrugInventoryRepository.save(toSave);
    }

    public PrescriptionBill createPrescriptionBill(PrescriptionBillDTO dto){
        return prescriptionBillRepository.save(PrescriptionBill.builder()
                        .prescription(Prescription.builder().id(dto.getPrescriptionId()).build())
                        .amount(dto.getAmount())
                        .paid(dto.getPaid())
                        .createTimestamp(LocalDateTime.now())
                        .updateTimestamp(LocalDateTime.now())
                .build());
    }

    public List<PrescriptionBill> getBillsByPatientId(Integer patientId){
        return prescriptionBillRepository.findAllByPatientId(patientId);
    }

    public List<PrescriptionBill> getBillsByPharmacy(Integer pharmacyId){
        return prescriptionBillRepository.findAllByPharmacyId(pharmacyId);
    }

    public PrescriptionBill payBill(Integer billId) {
        PrescriptionBill prescriptionBill = prescriptionBillRepository.findById(billId).orElseThrow();
        prescriptionBill.setPaid(true);
        return prescriptionBillRepository.save(prescriptionBill);
    }

}
