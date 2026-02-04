package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupSearchRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.query.EmployeeHeldupResponse;

public interface EmployeeHeldupQueryService {

    EmployeeHeldupResponse getInfoById(String uuid);
    PageResponse<EmployeeHeldupResponse> searchCriteria(EmployeeHeldupSearchRequest request);
}