package com.leads.microcube.payroll.organizationCashInformation;


import com.leads.microcube.Enum.Status;
import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treg_org_cash_information")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCashInformation extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "cash_registration_name", nullable = false)
    private String cashRegName;

    @Column(name = "cash_registration_number", nullable = false, length = 20)
    @NotBlank(message = "Cash Registration number is required")
    @Size(min = 20, max = 20, message = "Cash Registration number must be exactly 20 digits")
    @Pattern(regexp = "\\d{20}", message = "Cash Registration number must be exactly 20 digits")
    private String cashRegNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
