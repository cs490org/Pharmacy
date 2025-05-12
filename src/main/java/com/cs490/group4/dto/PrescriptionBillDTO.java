package com.cs490.group4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionBillDTO {

    private Integer prescriptionId;
    private BigDecimal amount;
    private Boolean paid;

}
