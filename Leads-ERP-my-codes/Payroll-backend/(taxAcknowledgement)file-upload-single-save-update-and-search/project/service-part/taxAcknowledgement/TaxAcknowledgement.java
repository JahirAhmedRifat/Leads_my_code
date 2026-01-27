package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tax_acknowledgement")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaxAcknowledgement extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @Column(name = " date_Of_submission", nullable = false)
    private LocalDate dateOfSubmission;

    @Column(name = "tin_attachment_path")
    private String tinAttachmentPath;

    @Column(name = "acknowledgement_path")
    private String acknowledgementPath;

    @Column(name = "serial_no")
    private String serialNo;

    @Lob
    @Column(name = "remarks")
    private String remarks;

}

