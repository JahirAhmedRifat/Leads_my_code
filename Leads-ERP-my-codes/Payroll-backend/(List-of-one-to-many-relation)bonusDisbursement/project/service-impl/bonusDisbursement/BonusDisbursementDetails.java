package com.leads.microcube.payroll.bonusDisbursement;


import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "treg_bonus_disbursement_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BonusDisbursementDetails extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "bonus_amount", nullable = false)
    private BigDecimal bonusAmount;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false)
    private BonusDisbursement bonusDisbursement;
}
