package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "treg_employee_heldup_details")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHeldupDetails extends BaseEntity {

    @Column(name = "heldup_master_id", nullable = false)
    @NotNull(message = "Heldup Master id is required")
    private Long heldupMasterId;

    @Column(name = "salary_structure_breakup_id", nullable = false)
    @NotNull(message = "Salary structure breakup id  is required")
    private Long salaryStructureBreakupId;

    @Column(name = "salary_head")
    private String salaryHead;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "heldup_value", nullable = false)
    @Min(value = 0, message = "Heldup value must be between 0% and 100%")
    @Max(value = 100, message = "Heldup value must be between 0% and 100%")
    private Integer heldupValue;
}
