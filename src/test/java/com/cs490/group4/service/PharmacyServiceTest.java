package com.cs490.group4.service;

import com.cs490.group4.dao.*;
import com.cs490.group4.dto.PrescriptionBillDTO;
import com.cs490.group4.security.User;
import com.cs490.group4.security.UserRepository;
import com.cs490.group4.dto.InventoryDTO;
import com.cs490.group4.dto.PharmacyCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceTest {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyDrugInventoryRepository pharmacyDrugInventoryRepository;

    @Mock
    private PrescriptionBillRepository prescriptionBillRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PharmacyService pharmacyService;

    @Test
    void getPharmacies_returnsAllPharmacies() {
        Pharmacy pharmacy1 = new Pharmacy();
        pharmacy1.setId(1);
        Pharmacy pharmacy2 = new Pharmacy();
        pharmacy2.setId(2);
        List<Pharmacy> expectedPharmacies = Arrays.asList(pharmacy1, pharmacy2);

        when(pharmacyRepository.findAll()).thenReturn(expectedPharmacies);

        List<Pharmacy> result = pharmacyService.getPharmacies();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(pharmacyRepository).findAll();
    }

    @Test
    void getPharmacyByUserId_returnsPharmacy() {
        Pharmacy mockPharmacy = new Pharmacy();
        mockPharmacy.setId(1);
        when(pharmacyRepository.findByUserId(1)).thenReturn(mockPharmacy);

        Pharmacy result = pharmacyService.getPharmacyByUserId(1);

        assertEquals(1, result.getId());
        verify(pharmacyRepository).findByUserId(1);
    }

    @Test
    void getPharmacyByUserId_throwsIfNotFound() {
        when(pharmacyRepository.findByUserId(99)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            pharmacyService.getPharmacyByUserId(99);
        });

        assertTrue(ex.getMessage().contains("pharmacist not defined"));
        verify(pharmacyRepository).findByUserId(99);
    }

    @Test
    void createPharmacy_successfullyCreatesPharmacy() {
        User mockUser = new User();
        mockUser.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        PharmacyCreateDTO dto = new PharmacyCreateDTO();
        dto.setUserId(1);
        dto.setName("Test Pharmacy");
        dto.setZipCode("12345");
        dto.setPhone("123-456-7890");
        dto.setAddress("123 Main St");

        Pharmacy savedPharmacy = new Pharmacy();
        savedPharmacy.setId(1);
        savedPharmacy.setUser(mockUser);
        savedPharmacy.setName(dto.getName());
        savedPharmacy.setZipCode(dto.getZipCode());
        savedPharmacy.setPhone(dto.getPhone());
        savedPharmacy.setAddress(dto.getAddress());

        when(pharmacyRepository.save(any(Pharmacy.class))).thenReturn(savedPharmacy);

        Pharmacy result = pharmacyService.createPharmacy(dto);

        assertEquals(1, result.getId());
        assertEquals("Test Pharmacy", result.getName());
        assertEquals("12345", result.getZipCode());
        assertEquals("123-456-7890", result.getPhone());
        assertEquals("123 Main St", result.getAddress());
        verify(userRepository).findById(1);
        verify(pharmacyRepository).save(any(Pharmacy.class));
    }

    @Test
    void createPharmacy_throwsIfUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        PharmacyCreateDTO dto = new PharmacyCreateDTO();
        dto.setUserId(99);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            pharmacyService.createPharmacy(dto);
        });

        assertTrue(ex.getMessage().contains("User Not Found"));
        verify(userRepository).findById(99);
    }

    @Test
    void isEmpty_returnsTrue() {
        when(pharmacyRepository.count()).thenReturn(0L);
        boolean result = pharmacyService.isEmpty();
        assertTrue(result);
    }

    @Test
    void isEmpty_returnsFalse() {
        when(pharmacyRepository.count()).thenReturn(1L);
        boolean result = pharmacyService.isEmpty();
        assertFalse(result);
    }

    @Test
    void getDrugsByPharmacy_returnsDrugs() {
        PharmacyDrugInventory inventory1 = new PharmacyDrugInventory();
        inventory1.setId(1);
        PharmacyDrugInventory inventory2 = new PharmacyDrugInventory();
        inventory2.setId(2);
        List<PharmacyDrugInventory> expectedInventory = Arrays.asList(inventory1, inventory2);

        when(pharmacyDrugInventoryRepository.findAllByPharmacyId(1)).thenReturn(expectedInventory);

        List<PharmacyDrugInventory> result = pharmacyService.getDrugsByPharmacy(1);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(pharmacyDrugInventoryRepository).findAllByPharmacyId(1);
    }

//    @Test
    void updateDrugInventory_successfullyUpdates() {
        InventoryDTO dto = new InventoryDTO();
        dto.setPharmacyId(1);
        dto.setDrugId(1);
        dto.setQuantity(100);

        when(pharmacyDrugInventoryRepository.findByDrugIdAndPharmacyId(1, 1))
                .thenReturn(null);

        pharmacyService.updateDrugInventory(dto);

        verify(pharmacyDrugInventoryRepository).save(any(PharmacyDrugInventory.class));
    }

    @Test
    void createPrescriptionBill_successfullyCreates() {
        PrescriptionBillDTO dto = new PrescriptionBillDTO();
        dto.setPrescriptionId(1);
        dto.setAmount(new BigDecimal("100.0"));
        dto.setPaid(false);

        PrescriptionBill savedBill = PrescriptionBill.builder()
                .id(1)
                .prescription(Prescription.builder().id(1).build())
                .amount(dto.getAmount())
                .paid(dto.getPaid())
                .createTimestamp(LocalDateTime.now())
                .updateTimestamp(LocalDateTime.now())
                .build();

        when(prescriptionBillRepository.save(any(PrescriptionBill.class))).thenReturn(savedBill);

        PrescriptionBill result = pharmacyService.createPrescriptionBill(dto);

        assertEquals(1, result.getId());
        assertEquals(new BigDecimal("100.0"), result.getAmount());
        assertFalse(result.getPaid());
        verify(prescriptionBillRepository).save(any(PrescriptionBill.class));
    }
} 