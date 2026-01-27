package com.leads.microcube.tax.taxRebatePolicy.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxRebatePolicyRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Tax Year is required")
    @Min(value = 1000, message = "Tax Year must be a 4-digit year")
    @Max(value = 9999, message = "Tax Year must be a 4-digit year")
    private Integer taxYear;

    @NotNull(message = "Policy Serial Number is required")
    private Integer policySerialNo;

    @NotNull(message = "Amount Type is required")
    private Long amountType;

    @NotNull(message = "Total Amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Total Amount cannot be negative")
    @DecimalMax(value = "1000000.00", inclusive = true, message = "Total Amount cannot exceed 10,00,000.00")
    @Digits(integer = 7, fraction = 2, message = "Total Amount can have up to 7 digits and 2 decimal places")
    private BigDecimal totalAmount;
}
