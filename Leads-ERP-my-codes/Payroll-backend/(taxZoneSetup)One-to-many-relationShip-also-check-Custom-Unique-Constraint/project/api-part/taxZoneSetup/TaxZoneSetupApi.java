package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.tax.taxZoneSetup.command.TaxZoneSetupRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface TaxZoneSetupApi {

    @PostMapping("/save")
    ResponseEntity<Envelope> createTaxZone(@Valid @RequestBody TaxZoneSetupRequest request);

    @PutMapping("/update")
    ResponseEntity<Envelope> updateTaxZone(@Valid @RequestBody TaxZoneSetupRequest request);

    @GetMapping("/taxZoneList/{taxZoneId}")
    ResponseEntity<Envelope> getTaxZoneList(@PathVariable Long taxZoneId);

}
