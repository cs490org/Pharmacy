package com.cs490.group4.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PrescriptionPharmacyLink {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", referencedColumnName = "id")
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", referencedColumnName = "id")
    private Pharmacy pharmacy;

    private LocalDateTime createTimestamp, updateTimestamp;

}