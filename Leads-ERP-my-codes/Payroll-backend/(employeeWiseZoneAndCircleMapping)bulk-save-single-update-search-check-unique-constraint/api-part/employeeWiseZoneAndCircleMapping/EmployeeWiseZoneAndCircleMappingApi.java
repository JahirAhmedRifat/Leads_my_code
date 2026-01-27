package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeIdAndTaxYearRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleMappingRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleSearchRequest;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebateSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface EmployeeWiseZoneAndCircleMappingApi {

    @PostMapping("/bulkSave")
    ResponseEntity<Envelope> bulkSave(@Valid @RequestBody List<EmployeeWiseZoneAndCircleMappingRequest> requests);

    @PutMapping("/update")
    ResponseEntity<Envelope> updateMapping(@Valid @RequestBody EmployeeWiseZoneAndCircleMappingRequest request);

    @PostMapping("/findByEmpIdAndTaxYear")
    ResponseEntity<Envelope> getDataByEmpIdAndTaxYear(@RequestBody EmployeeIdAndTaxYearRequest request);

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody EmployeeWiseZoneAndCircleSearchRequest request);

}
