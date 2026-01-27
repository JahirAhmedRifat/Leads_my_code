package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.tax.taxZoneSetup.command.TaxZoneSetupRequest;
import com.leads.microcube.tax.taxZoneSetup.query.TaxZoneSetupResponse;

public interface TaxZoneSetupService {
    TaxZoneSetupResponse createTaxZone(TaxZoneSetupRequest request);
    TaxZoneSetupResponse updateTaxZone(TaxZoneSetupRequest request);
}
