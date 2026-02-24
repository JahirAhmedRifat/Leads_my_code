package com.leads.microcube.payroll.organizationCashInformation.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCashInformationRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Company Id is required")
    private Long companyId;

    @NotNull(message = "Branch Id is required")
    private Long branchId;

    @NotBlank(message = "Cash Registration Name is required")
    private String cashRegName;

    @NotBlank(message = "Cash Registration number is required")
    @Pattern(regexp = "\\d{20}", message = "Cash Registration number must be exactly 20 digits")
    @Size(min = 20, max = 20, message = "Cash Registration number must be exactly 20 digits")
    private String cashRegNumber;

    @NotBlank(message = "Status is required")
    private String status;

}
