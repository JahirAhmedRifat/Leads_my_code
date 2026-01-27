package com.leads.microcube.tax.taxAcknowledgement.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxAcknowledgementRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Tax Year is required")
    @Min(value = 1000, message = "Tax Year must be a 4-digit year")
    @Max(value = 9999, message = "Tax Year must be a 4-digit year")
    private Integer taxYear;

    @NotNull(message = "Date submission is required")
    private LocalDate dateOfSubmission;
    private String serialNo;
    private String remarks;

    private MultipartFile tinAttachmentPath;
    private MultipartFile acknowledgementPath;
}
