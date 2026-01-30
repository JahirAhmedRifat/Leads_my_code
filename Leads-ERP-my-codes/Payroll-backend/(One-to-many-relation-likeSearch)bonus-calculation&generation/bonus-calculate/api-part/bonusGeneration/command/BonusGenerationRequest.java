package com.leads.microcube.process.bonusGeneration.command;

import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BonusGenerationRequest extends IdHolder {

    @NotBlank(message = "Organization Id is required")
    private String empOrgId;

    private String empBranchId;

    @Min(value = 1000, message = "Bonus Year must be a 4-digit year")
    @Max(value = 9999, message = "Bonus Year must be a 4-digit year")
    private Integer bonusYear;

    private String bonusType;
    private LocalDate bonusDate;

}
