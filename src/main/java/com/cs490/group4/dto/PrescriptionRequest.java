package com.cs490.group4.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrescriptionRequest {

    private Integer doctorId, patientId, drugId;
    private LocalDateTime rxExpiryTimestamp;

}
