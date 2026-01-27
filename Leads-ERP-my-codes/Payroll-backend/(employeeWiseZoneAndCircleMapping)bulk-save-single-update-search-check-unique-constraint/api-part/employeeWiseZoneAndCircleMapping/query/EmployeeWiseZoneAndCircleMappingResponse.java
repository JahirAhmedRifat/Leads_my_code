package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.Data;

@Data
public class EmployeeWiseZoneAndCircleMappingResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Integer taxYear;
    private String fiscalYear;
    private String employeeId;
    private String name;
    private Long taxZone;
    private Long taxCircle;
}
