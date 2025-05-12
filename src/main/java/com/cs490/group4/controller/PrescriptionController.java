package com.cs490.group4.controller;

import com.cs490.group4.dao.Prescription;
import com.cs490.group4.dto.PrescriptionRequest;
import com.cs490.group4.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;


    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getPrescriptions(){
        return ResponseEntity.ok(prescriptionService.getPrescriptions());
    }

    @PostMapping("/prescription")
    private ResponseEntity<Prescription> createPrescription(@RequestBody PrescriptionRequest prescriptionRequest){
        return ResponseEntity.ok(prescriptionService.createPrescription(prescriptionRequest));
    }

    @PatchMapping("/{id}/status")
        public ResponseEntity<Prescription> updateStatus(
                @PathVariable Integer id,
                @RequestParam String status) {
            Prescription updated = prescriptionService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
    }

}
