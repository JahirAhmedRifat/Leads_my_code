package com.leads.microcube.payroll.yearlyBonusSetup;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "yearly_bonus_setup")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class YearlyBonusSetup extends BaseEntity {

    @Column(name = "bonus_year", nullable = false)
    private Integer bonusYear;

    @Column(name = "bonus_policy", nullable = false)
    private Long bonusPolicy;

    @Column(name = "bonus_type", nullable = false)
    private String bonusType;

    @Column(name = "bonus_date", nullable = false)
    private LocalDate bonusDate;

    @Column(name = "all_religions")
    private Boolean allReligions;

    @Column(name = "religion")
    private Long religion;

    @Column(name = "taxable")
    private Boolean taxable;

    @Column(name = "is_adjust_with_salary")
    private Boolean isAdjustWithSalary;

    @Column(name = "salary_year")
    private Integer salaryYear;

    @Column(name = "salary_month")
    private String salaryMonth;

    @Column(name = "is_appraisal_ratio")
    private Boolean isAppraisalRatio;

    @Column(name = "appraisal_period")
    private Long appraisalPeriod;

    @Column(name = "appraisal_phase")
    private String appraisalPhase;
}
