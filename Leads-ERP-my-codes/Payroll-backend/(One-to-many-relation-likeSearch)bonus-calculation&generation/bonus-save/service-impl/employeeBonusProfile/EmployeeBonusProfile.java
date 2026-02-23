package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee_bonus_profile")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeBonusProfile extends BaseEntity {

    @Column(name = "emp_org_id", nullable = false)
    private String empOrgId;

    @Column(name = "emp_branch_id")
    private String empBranchId;

    @Column(name = "bonus_year", nullable = false)
    private Integer bonusYear;

    @Column(name = "bonus_type", nullable = false)
    private String bonusType;

    @Column(name = "bonus_date", nullable = false)
    private LocalDate bonusDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "yearly_bonus_uuid", nullable = false)
    private String yearlyBonusUuid;

    @OneToMany(mappedBy = "employeeBonusProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeBonusProfileDetails> bonusProfileDetails = new ArrayList<>();

}
