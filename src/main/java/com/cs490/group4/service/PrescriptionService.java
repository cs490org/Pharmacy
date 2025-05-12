package com.cs490.group4.service;

import com.cs490.group4.dao.*;
import com.cs490.group4.dto.PrescriptionRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private PatientPharmacyRepository patientPharmacyRepository;
    @Autowired
    private PrescriptionBillRepository prescriptionBillRepository;

    public List<Prescription> getPrescriptions(){
        return prescriptionRepository.findAll();
    }

    public List<Prescription> getPrescriptionsByPharmacy(Integer pharmacyId){
            return prescriptionRepository.findAllRxByPharmacyId(pharmacyId);
    }

    @Transactional
        public Prescription updateStatus(Integer id, String statusCode) {
            Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));
            p.setRxStatusCode(RxStatusCode.valueOf(statusCode));
            p.setUpdateTimestamp(LocalDateTime.now());
            return prescriptionRepository.save(p);
    }

    @Transactional
    public Prescription createPrescription(PrescriptionRequest dto){
        Doctor doctor =  doctorRepository.findById(dto.getDoctorId()).orElseThrow(()-> new EntityNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(()-> new EntityNotFoundException("Patient not found"));
        Drug drug = drugRepository.findById(dto.getDrugId()).orElseThrow(()-> new EntityNotFoundException("Drug not found"));

        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setDrug(drug);
        prescription.setRxStatusCode(RxStatusCode.NEW_PRESCRIPTION);
        prescription.setRxExpiryTimestamp(dto.getRxExpiryTimestamp());
        prescription.setCreateTimestamp(LocalDateTime.now());
        prescription.setUpdateTimestamp(LocalDateTime.now());

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        List<PatientPharmacy> pharmacies = patientPharmacyRepository.findByPatientIdOrderByCreateTimestampDesc(patient.getId());

            if (!pharmacies.isEmpty()) {
                // Create bill only if a pharmacy is found
                PrescriptionBill bill = PrescriptionBill.builder()
                    .prescription(savedPrescription)
                    .amount(drug.getPrice()) // use drug price directly
                    .paid(false)
                    .createTimestamp(LocalDateTime.now())
                    .updateTimestamp(LocalDateTime.now())
                    .build();

                prescriptionBillRepository.save(bill);
            }
        return savedPrescription;
    }

    public boolean isEmpty(){
        return prescriptionRepository.findAll().isEmpty();
    }

}
