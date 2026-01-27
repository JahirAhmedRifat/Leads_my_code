package com.leads.microcube.process.bonusGeneration.command;

import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BonusGenerationRequest extends IdHolder {
    private String employeeId;
    private Boolean isAllEmployees;
    private Long branchId;

    @Min(value = 1000, message = "Bonus Year must be a 4-digit year")
    @Max(value = 9999, message = "Bonus Year must be a 4-digit year")
    private Integer bonusYear;

    private Long bonusPolicy;
    private String bonusType;
}
