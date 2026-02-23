package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementRequest;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface BonusDisbursementApi {

    @PostMapping("/bulk-save")
    ResponseEntity<?> bulkSave(@Valid @RequestBody List<BonusDisbursementRequest> requests);

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody BonusDisbursementSearchRequest request);

}
