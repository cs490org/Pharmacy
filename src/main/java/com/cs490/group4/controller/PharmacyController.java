package com.cs490.group4.controller;

import com.cs490.group4.dao.Drug;
import com.cs490.group4.dao.Pharmacy;
import com.cs490.group4.dao.Prescription;
import com.cs490.group4.dao.PrescriptionBill;
import com.cs490.group4.dto.InventoryDTO;
import com.cs490.group4.dto.PharmacyCreateDTO;
import com.cs490.group4.dto.PrescriptionBillDTO;
import com.cs490.group4.service.DrugService;
import com.cs490.group4.service.PharmacyService;
import com.cs490.group4.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    private final DrugService drugService;

    private final PrescriptionService prescriptionService;



    @GetMapping()
    private ResponseEntity<?> getPharmacies(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return ResponseEntity.ok(pharmacyService.getPharmacyByUserId(userId));
        } else {
            return ResponseEntity.ok(pharmacyService.getPharmacies());
        }
    }

    @GetMapping("/nearby")
    public List<Pharmacy> getNearbyPharmacies(@RequestParam String zipCode) {
        return pharmacyService.findNearbyPharmacies(zipCode);
    }


    @PostMapping
    private ResponseEntity<?> createPharmacy(@RequestBody PharmacyCreateDTO pharmacyCreateDTO) {
        return ResponseEntity.ok(pharmacyService.createPharmacy(pharmacyCreateDTO));
    }

    @PatchMapping
    private ResponseEntity<?> updatePharmacy(@RequestBody PharmacyCreateDTO pharmacyCreateDTO) {
        return ResponseEntity.ok(pharmacyService.updatePharmacy(pharmacyCreateDTO));
    }

    @GetMapping("/drugs")
    private ResponseEntity<?> getDrugs(@RequestParam Integer pharmacyId) {
        return ResponseEntity.ok(pharmacyService.getDrugsByPharmacy(pharmacyId));
    }

    @PatchMapping("/drugs/inventory")
    private ResponseEntity<?> updateInventory(@RequestBody InventoryDTO dto) {
        pharmacyService.updateDrugInventory(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bill")
    private ResponseEntity<PrescriptionBill> createBill(@RequestBody PrescriptionBillDTO dto){
        return ResponseEntity.ok(pharmacyService.createPrescriptionBill(dto));
    }

    @GetMapping("/bills")
    private ResponseEntity<List<PrescriptionBill>> getBillsForPatient(@RequestParam Integer patientId){
        return ResponseEntity.ok(pharmacyService.getBillsByPatientId(patientId));
    }

    @GetMapping("/allbills")
        private ResponseEntity<List<PrescriptionBill>> getAllBillsByPharmacy(@RequestParam Integer pharmacyId){
            return ResponseEntity.ok(pharmacyService.getBillsByPharmacy(pharmacyId));
    }

    @PatchMapping("/bill")
    private ResponseEntity<PrescriptionBill> payBill(@RequestParam Integer billId) {
        return ResponseEntity.ok(pharmacyService.payBill(billId));
    }

    @GetMapping("/unassigned")  //to fetch new drugs not in pharmacy inventory already
    private ResponseEntity<List<Drug>> getUnassignedDrugs(@RequestParam Integer pharmacyId) {
        return ResponseEntity.ok(drugService.getUnassignedDrugs(pharmacyId));
    }

    @PostMapping("/assign-drug")
    public ResponseEntity<?> assignDrugToPharmacy(@RequestBody InventoryDTO dto) {
        pharmacyService.updateDrugInventory(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rx")  //to fetch all prescriptions for pharmacy
        private ResponseEntity<List<Prescription>> getPrescriptionsByPharmacy(@RequestParam Integer pharmacyId) {
            return ResponseEntity.ok(prescriptionService.getPrescriptionsByPharmacy(pharmacyId));
    }

}
