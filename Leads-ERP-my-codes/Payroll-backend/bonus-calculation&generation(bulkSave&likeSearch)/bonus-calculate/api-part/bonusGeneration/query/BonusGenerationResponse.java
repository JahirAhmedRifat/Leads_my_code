package com.leads.microcube.process.bonusGeneration.query;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BonusGenerationResponse {
    private String employeeId;
    private Integer designation;
    private Integer department;
    private BigDecimal bonusAmount;
    private LocalDate bonusDate;
    private String bonusId;
    private Integer bonusYear;
}
