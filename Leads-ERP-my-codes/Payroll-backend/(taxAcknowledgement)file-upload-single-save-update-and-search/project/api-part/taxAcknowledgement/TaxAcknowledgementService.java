package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementRequest;
import com.leads.microcube.tax.taxAcknowledgement.query.TaxAcknowledgementResponse;

public interface TaxAcknowledgementService {
    TaxAcknowledgementResponse saveData(TaxAcknowledgementRequest request);
    TaxAcknowledgementResponse updateData(TaxAcknowledgementRequest request);
}
