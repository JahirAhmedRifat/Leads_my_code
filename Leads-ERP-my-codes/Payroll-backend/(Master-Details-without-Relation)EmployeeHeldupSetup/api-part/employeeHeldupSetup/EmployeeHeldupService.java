package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.query.EmployeeHeldupResponse;

public interface EmployeeHeldupService {
    EmployeeHeldupResponse heldUpCreate(EmployeeHeldupRequest request);
    EmployeeHeldupResponse updateHeldup(EmployeeHeldupRequest request);
}