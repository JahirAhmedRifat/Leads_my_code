package com.leads.microcube.tax.taxAcknowledgement.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaxAcknowledgementResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private String employeeId;
    private Integer taxYear;
    private LocalDate dateOfSubmission;
    private String tinAttachmentPath;
    private String acknowledgementPath;
    private String serialNo;
    private String remarks;
}
