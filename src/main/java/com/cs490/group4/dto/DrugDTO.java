package com.cs490.group4.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrugDTO {
    private String name;
    private String description;
    private String dosage;
    private BigDecimal price;
    private String image;
}
