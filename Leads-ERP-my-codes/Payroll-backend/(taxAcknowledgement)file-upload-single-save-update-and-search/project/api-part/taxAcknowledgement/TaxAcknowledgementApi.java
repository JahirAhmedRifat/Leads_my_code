package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementRequest;
import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementSearchRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface TaxAcknowledgementApi {

    @PostMapping("/save")
    ResponseEntity<Envelope> saveData(@Valid @ModelAttribute TaxAcknowledgementRequest request);

    @PutMapping("/update")
    ResponseEntity<Envelope> updateData(@Valid @ModelAttribute TaxAcknowledgementRequest request);

    @PostMapping("/search")
    ResponseEntity<?> searchCriteria(@RequestBody TaxAcknowledgementSearchRequest request);

}
