package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebatePolicyRequest;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebateSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TaxRebatePolicyApi {

    @PostMapping("/bulkSave")
    ResponseEntity<Envelope> bulkSave(@Valid @RequestBody List<TaxRebatePolicyRequest> requests);

    @PutMapping("/update")
    ResponseEntity<Envelope> updateMapping(@Valid @RequestBody TaxRebatePolicyRequest request);

    @PostMapping("/search")
    ResponseEntity<?> searchTaxRebate(@RequestBody TaxRebateSearchRequest request);
}
