package com.leads.microcube.payroll.employeeBonusProfile;


import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "employee_bonus_profile_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeBonusProfileDetails extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "bonus_amount", nullable = false)
    private BigDecimal bonusAmount;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bonus_profile_id", nullable = false)
    private EmployeeBonusProfile employeeBonusProfile;
}
