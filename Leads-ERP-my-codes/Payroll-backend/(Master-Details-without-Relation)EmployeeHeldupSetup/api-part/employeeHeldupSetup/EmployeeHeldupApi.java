package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface EmployeeHeldupApi {

    @PostMapping("/save")
    ResponseEntity<?> heldUpCreate(@Valid @RequestBody EmployeeHeldupRequest request);

    @PutMapping("/update")
    ResponseEntity<?> updateHeldup(@Valid @RequestBody EmployeeHeldupRequest request);

    @GetMapping("/get-data/{uuid}")
    ResponseEntity<?> getInfoByUuid(@PathVariable String uuid);

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody EmployeeHeldupSearchRequest request);
}
