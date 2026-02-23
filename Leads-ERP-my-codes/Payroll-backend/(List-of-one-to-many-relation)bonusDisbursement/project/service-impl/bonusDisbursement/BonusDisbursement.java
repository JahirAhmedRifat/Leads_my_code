package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treg_bonus_disbursement")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BonusDisbursement extends BaseEntity {

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

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "disbursement_date", nullable = false)
    private LocalDate disbursementDate;

    @OneToMany(mappedBy = "bonusDisbursement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonusDisbursementDetails> bonusDisbursementDetails = new ArrayList<>();

}
