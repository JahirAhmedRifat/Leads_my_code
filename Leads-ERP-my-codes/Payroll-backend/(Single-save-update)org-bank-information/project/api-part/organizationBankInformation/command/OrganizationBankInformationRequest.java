package com.leads.microcube.payroll.organizationBankInformation.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationBankInformationRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Company Id is required")
    private Long companyId;

    @NotNull(message = "Branch Id is required")
    private Long branchId;

    @NotBlank(message = "Routing number is required")
    @Pattern(
            regexp = "\\d{4,12}",
            message = "Routing number must contain only digits and be between 4 to 12 digits"
    )
    private String routingNumber;

    @NotNull(message = "Bank Name is required")
    private Long bankName;

    @NotNull(message = "Bank Branch is required")
    private Long bankBranch;

    @NotNull(message = "Account Type is required")
    private Long accountType;

    @NotBlank(message = "Account Name is required")
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Pattern(
            regexp = "\\d{10,16}",
            message = "Account number must contain only digits and be between 10 to 16 digits"
    )
    private String accountNumber;

    @NotNull(message = "Currency is required")
    private Long currency;

    @NotBlank(message = "GL Account number is required")
    @Pattern(regexp = "\\d{20}", message = "GL Account number must be exactly 20 digits")
    private String glAccountNo;

    @NotBlank(message = "Status is required")
    private String status;

}
