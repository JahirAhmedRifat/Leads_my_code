package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public interface EmployeeBonusProfileApi {

    @PostMapping("/save")
    ResponseEntity<?> bonusCreate(@Valid @RequestBody EmployeeBonusProfileRequest request);

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody EmployeeBonusProfileSearchRequest request);
}
