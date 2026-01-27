package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWiseZoneAndCircleMappingRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Tax Year is required")
    @Min(value = 1000, message = "Tax Year must be a 4-digit year")
    @Max(value = 9999, message = "Tax Year must be a 4-digit year")
    private Integer taxYear;

    @NotBlank(message = "Fiscal Year is required")
    private String fiscalYear;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String name;

    @NotNull(message = "Tax Zone is required")
    private Long taxZone;

    @NotNull(message = "Tax Circle is required")
    private Long taxCircle;

}
