package com.cs490.group4.controller;

import com.cs490.group4.dao.Prescription;
import com.cs490.group4.dto.PrescriptionRequest;
import com.cs490.group4.service.PrescriptionService;
import com.cs490.group4.service.PrescriptionEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionEventService prescriptionEventService;

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
        
        // Send event when prescription is fulfilled
        if ("FULFILLED".equalsIgnoreCase(status)) {
            prescriptionEventService.sendPrescriptionFulfilledEvent(updated);
        }
        
        return ResponseEntity.ok(updated);
    }
}
