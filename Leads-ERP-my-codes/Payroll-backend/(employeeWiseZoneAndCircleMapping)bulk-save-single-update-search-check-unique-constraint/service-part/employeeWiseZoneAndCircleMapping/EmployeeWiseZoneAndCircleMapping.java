package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_wise_zone_and_circle_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeWiseZoneAndCircleMapping extends BaseEntity {

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @Column(name = "fiscal_year", nullable = false)
    private String fiscalYear;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "employee_name")
    private String name;

    @Column(name = "tax_zone", nullable = false)
    private Long taxZone;

    @Column(name = "tax_circle", nullable = false)
    private Long taxCircle;

}

