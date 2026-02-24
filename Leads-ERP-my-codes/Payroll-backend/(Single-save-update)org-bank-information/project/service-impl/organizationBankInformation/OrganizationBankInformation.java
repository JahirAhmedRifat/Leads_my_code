package com.leads.microcube.payroll.organizationBankInformation;


import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treg_org_bank_information")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationBankInformation extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "routing_number", nullable = false, length = 12)
    @NotBlank(message = "Routing number is required")
    @Pattern(
            regexp = "\\d{4,12}",
            message = "Routing number must contain only digits and be between 4 to 12 digits"
    )
    private String routingNumber;

    @Column(name = "bank_name", nullable = false)
    private Long bankName;

    @Column(name = "bank_branch", nullable = false)
    private Long bankBranch;

    @Column(name = "account_type", nullable = false)
    private Long accountType;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_number", nullable = false, length = 16)
    @NotBlank(message = "Account number is required")
    @Pattern(
            regexp = "\\d{10,16}",
            message = "Account number must contain only digits and be between 10 to 16 digits"
    )
    private String accountNumber;

    @Column(name = "currency", nullable = false)
    private Long currency;

    @Column(name = "gl_account_number", nullable = false, length = 20)
    @NotBlank
    @Pattern(regexp = "\\d{20}", message = "GL Account number must be exactly 20 digits")
    private String glAccountNo;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
