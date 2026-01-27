package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleMappingRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.query.EmployeeWiseZoneAndCircleMappingResponse;
import java.util.List;

public interface EmployeeWiseZoneAndCircleMappingService {
    List<EmployeeWiseZoneAndCircleMappingResponse> bulkSave(List<EmployeeWiseZoneAndCircleMappingRequest> requests);
    EmployeeWiseZoneAndCircleMappingResponse updateMapping(EmployeeWiseZoneAndCircleMappingRequest requests);
}
