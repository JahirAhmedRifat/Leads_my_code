package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private HeldupType heldupType;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Lob
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "total_heldup_amount")
    private Double totalHeldupAmount = 0D;
}
