package com.cs490.group4.service;

import com.cs490.group4.dao.*;
import com.cs490.group4.dao.DoctorRepository;
import com.cs490.group4.dao.PrescriptionRepository;
import com.cs490.group4.dto.PrescriptionRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    @Test
    void getPrescriptions_returnsAllPrescriptions() {
        Prescription prescription1 = new Prescription();
        prescription1.setId(1);
        Prescription prescription2 = new Prescription();
        prescription2.setId(2);
        List<Prescription> expectedPrescriptions = Arrays.asList(prescription1, prescription2);

        when(prescriptionRepository.findAll()).thenReturn(expectedPrescriptions);

        List<Prescription> result = prescriptionService.getPrescriptions();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(prescriptionRepository).findAll();
    }

//    @Test
    void createPrescription_successfullyCreatesPrescription() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1);
        Patient mockPatient = new Patient();
        mockPatient.setId(1);
        Drug mockDrug = new Drug();
        mockDrug.setId(1);

        when(doctorRepository.findById(1)).thenReturn(Optional.of(mockDoctor));
        when(patientRepository.findById(1)).thenReturn(Optional.of(mockPatient));
        when(drugRepository.findById(1)).thenReturn(Optional.of(mockDrug));

        PrescriptionRequest dto = new PrescriptionRequest();
        dto.setDoctorId(1);
        dto.setPatientId(1);
        dto.setDrugId(1);
        dto.setRxExpiryTimestamp(LocalDateTime.now().plusDays(30));

        Prescription savedPrescription = new Prescription();
        savedPrescription.setId(1);
        savedPrescription.setDoctor(mockDoctor);
        savedPrescription.setPatient(mockPatient);
        savedPrescription.setDrug(mockDrug);
        savedPrescription.setRxStatusCode(RxStatusCode.NEW_PRESCRIPTION);
        savedPrescription.setRxExpiryTimestamp(dto.getRxExpiryTimestamp());
        savedPrescription.setCreateTimestamp(LocalDateTime.now());
        savedPrescription.setUpdateTimestamp(LocalDateTime.now());

        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(savedPrescription);

        Prescription result = prescriptionService.createPrescription(dto);

        assertEquals(1, result.getId());
        assertEquals(mockDoctor, result.getDoctor());
        assertEquals(mockPatient, result.getPatient());
        assertEquals(mockDrug, result.getDrug());
        assertEquals(RxStatusCode.NEW_PRESCRIPTION, result.getRxStatusCode());
        verify(doctorRepository).findById(1);
        verify(patientRepository).findById(1);
        verify(drugRepository).findById(1);
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void createPrescription_throwsIfDoctorNotFound() {
        when(doctorRepository.findById(99)).thenReturn(Optional.empty());

        PrescriptionRequest dto = new PrescriptionRequest();
        dto.setDoctorId(99);

        assertThrows(EntityNotFoundException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        verify(doctorRepository).findById(99);
    }

    @Test
    void createPrescription_throwsIfPatientNotFound() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1);
        when(doctorRepository.findById(1)).thenReturn(Optional.of(mockDoctor));
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        PrescriptionRequest dto = new PrescriptionRequest();
        dto.setDoctorId(1);
        dto.setPatientId(99);

        assertThrows(EntityNotFoundException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        verify(doctorRepository).findById(1);
        verify(patientRepository).findById(99);
    }

    @Test
    void createPrescription_throwsIfDrugNotFound() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1);
        Patient mockPatient = new Patient();
        mockPatient.setId(1);
        when(doctorRepository.findById(1)).thenReturn(Optional.of(mockDoctor));
        when(patientRepository.findById(1)).thenReturn(Optional.of(mockPatient));
        when(drugRepository.findById(99)).thenReturn(Optional.empty());

        PrescriptionRequest dto = new PrescriptionRequest();
        dto.setDoctorId(1);
        dto.setPatientId(1);
        dto.setDrugId(99);

        assertThrows(EntityNotFoundException.class, () -> {
            prescriptionService.createPrescription(dto);
        });

        verify(doctorRepository).findById(1);
        verify(patientRepository).findById(1);
        verify(drugRepository).findById(99);
    }

    @Test
    void isEmpty_returnsTrue() {
        when(prescriptionRepository.findAll()).thenReturn(List.of());
        boolean result = prescriptionService.isEmpty();
        assertTrue(result);
    }

    @Test
    void isEmpty_returnsFalse() {
        Prescription prescription = new Prescription();
        prescription.setId(1);
        when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));
        boolean result = prescriptionService.isEmpty();
        assertFalse(result);
    }
} 