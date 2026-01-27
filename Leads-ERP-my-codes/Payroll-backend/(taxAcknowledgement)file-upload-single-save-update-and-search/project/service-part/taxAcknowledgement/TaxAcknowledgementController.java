package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementRequest;
import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementSearchRequest;
import com.leads.microcube.tax.taxAcknowledgement.query.TaxAcknowledgementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiController
@RequestMapping("/tax-acknowledgement")
public class TaxAcknowledgementController
        extends BaseController<TaxAcknowledgementRequest, TaxAcknowledgementResponse>
        implements TaxAcknowledgementApi {

    private final TaxAcknowledgementService taxacknowledgementService;
    private final TaxAcknowledgementQueryService taxacknowledgementQueryService;

    public TaxAcknowledgementController(
            BaseService<TaxAcknowledgementRequest, TaxAcknowledgementResponse> service,
            BaseQueryService<TaxAcknowledgementRequest, TaxAcknowledgementResponse> queryService,
            TaxAcknowledgementService taxacknowledgementService,
            TaxAcknowledgementQueryService taxacknowledgementQueryService
    ) {
        super(service, queryService);
        this.taxacknowledgementService = taxacknowledgementService;
        this.taxacknowledgementQueryService = taxacknowledgementQueryService;
    }

    @Override
    public ResponseEntity<Envelope> saveData(TaxAcknowledgementRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Data saved successfully")
                .setPayload(taxacknowledgementService.saveData(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> updateData(TaxAcknowledgementRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Data update successfully")
                .setPayload(taxacknowledgementService.updateData(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<?> searchCriteria(TaxAcknowledgementSearchRequest request) {
        PageResponse<TaxAcknowledgementResponse> result = taxacknowledgementQueryService.searchCriteria(
                request.getEmployeeId(),
                request.getTaxYear(),
                request.getPageIndex(),
                request.getPageSize()
        );

        if (result.getContent().isEmpty()) {
            return ResponseEntity.ok(
                    CommonResponse.makeResponse(
                            null,
                            "No Data found",
                            false
                    )
            );
        }

        return ResponseEntity.ok(
                CommonResponse.makeResponse(
                        result,
                        "Data fetched successfully",
                        true
                )
        );
    }
}
