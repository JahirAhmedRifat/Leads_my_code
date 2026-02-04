package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "treg_employee_heldup")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHeldup extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "heldup_type", nullable = false)
    private Long heldupType;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Lob
    @Column(name = "remarks")
    private String remarks;
}
