package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.tax.taxZoneSetup.query.TaxZoneSetupResponse;

public interface TaxZoneSetupQueryService {
    TaxZoneSetupResponse getTaxZoneByTaxId(Long taxZoneId);
}
