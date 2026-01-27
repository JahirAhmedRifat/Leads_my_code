package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee_bonus_profile")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeBonusProfile extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "designation", nullable = false)
    private Long designation;

    @Column(name = "department", nullable = false)
    private Long department;

    @Column(name = "bonus_amount", nullable = false)
    private BigDecimal bonusAmount;

    @Column(name = "bonus_date", nullable = false)
    private LocalDate bonusDate;

    @Column(name = "bonus_year", nullable = false)
    private Integer bonusYear;

    @Column(name = "yearly_bonus_uuid", nullable = false)
    private String yearlyBonusUuid;

    private String status;

}

