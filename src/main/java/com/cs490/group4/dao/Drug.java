package com.cs490.group4.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Drug {

    @Id
    @GeneratedValue
    private Integer id;
    private String name, description, dosage;

    private BigDecimal price;
    private String image; 

    private LocalDateTime createTimestamp, updateTimestamp;

}