package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.tax.taxZoneSetup.command.TaxZoneSetupRequest;
import com.leads.microcube.tax.taxZoneSetup.query.TaxZoneSetupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiController
@RequestMapping("/tax-zone-setup")
public class TaxZoneSetupController
    extends BaseController<TaxZoneSetupRequest, TaxZoneSetupResponse>
    implements TaxZoneSetupApi {

    private final TaxZoneSetupService taxZoneSetupService;
    private final TaxZoneSetupQueryService taxZoneSetupQueryService;

    public TaxZoneSetupController(
            BaseService<TaxZoneSetupRequest, TaxZoneSetupResponse> service,
            BaseQueryService<TaxZoneSetupRequest, TaxZoneSetupResponse> queryService,
            TaxZoneSetupService taxZoneSetupService,
            TaxZoneSetupQueryService taxZoneSetupQueryService
    ) {
        super(service, queryService);
        this.taxZoneSetupService = taxZoneSetupService;
        this.taxZoneSetupQueryService = taxZoneSetupQueryService;
    }


    @Override
    public ResponseEntity<Envelope> createTaxZone(TaxZoneSetupRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("TaxZone save successfully")
                .setPayload(taxZoneSetupService.createTaxZone(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> updateTaxZone(TaxZoneSetupRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("TaxZone updated successfully")
                .setPayload(taxZoneSetupService.updateTaxZone(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> getTaxZoneList(Long taxZoneId) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("TaxZone fetched successfully")
                .setPayload(taxZoneSetupQueryService.getTaxZoneByTaxId(taxZoneId));

        return ResponseEntity.ok(envelope);
    }

}
