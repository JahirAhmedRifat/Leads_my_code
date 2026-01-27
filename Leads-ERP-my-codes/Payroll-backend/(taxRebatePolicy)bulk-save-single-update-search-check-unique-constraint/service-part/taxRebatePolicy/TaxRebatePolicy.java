package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tax_rebate_policy")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaxRebatePolicy extends BaseEntity {

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @Column(name = "policy_serial_no", nullable = false)
    private Integer policySerialNo;

    @Column(name = "amount_type", nullable = false)
    private Long amountType;

    @Column(name = "total_amount", nullable = false, precision = 9, scale = 2)
    private BigDecimal totalAmount;
}

