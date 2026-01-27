package com.leads.microcube.tax.taxRebatePolicy.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TaxRebatePolicyResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Integer taxYear;
    private Integer policySerialNo;
    private Long amountType;
    private BigDecimal totalAmount;
}
