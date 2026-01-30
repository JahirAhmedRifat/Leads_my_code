package com.leads.microcube.payroll.yearlyBonusSetup;


import com.leads.microcube.payroll.yearlyBonusSetup.command.YearlyBonusSetupSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface YearlyBonusSetupApi {

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody YearlyBonusSetupSearchRequest request);

    @PostMapping("/getData")
    ResponseEntity<?> findByBonusYearAndPolicyAndType(@RequestBody YearlyBonusSetupSearchRequest request);

    @PostMapping("/getInfo")
    ResponseEntity<?> findByBonusYearAndType(@RequestBody YearlyBonusSetupSearchRequest request);
}
