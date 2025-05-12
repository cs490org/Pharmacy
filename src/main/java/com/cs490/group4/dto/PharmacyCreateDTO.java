package com.cs490.group4.dto;

import lombok.Data;

@Data
public class PharmacyCreateDTO {
    private Integer userId;
    private String name;
    private String zipCode;
    private String phone;
    private String address;
}
