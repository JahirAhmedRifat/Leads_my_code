package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.BaseController;
import com.leads.microcube.base.BaseQueryService;
import com.leads.microcube.base.BaseService;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebatePolicyRequest;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebateSearchRequest;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@ApiController
@RequestMapping("/tax-rebate-policy")
public class TaxRebatePolicyController
        extends BaseController<TaxRebatePolicyRequest, TaxRebatePolicyResponse>
        implements TaxRebatePolicyApi {

    private final TaxRebatePolicyService taxRebatePolicyService;
    private final TaxRebatePolicyQueryService taxRebatePolicyQueryService;

    public TaxRebatePolicyController(
            BaseService<TaxRebatePolicyRequest, TaxRebatePolicyResponse> service,
            BaseQueryService<TaxRebatePolicyRequest, TaxRebatePolicyResponse> queryService,
            TaxRebatePolicyService taxRebatePolicyService,
            TaxRebatePolicyQueryService taxRebatePolicyQueryService
    ) {
        super(service, queryService);
        this.taxRebatePolicyService = taxRebatePolicyService;
        this.taxRebatePolicyQueryService = taxRebatePolicyQueryService;
    }

    @Override
    public ResponseEntity<Envelope> bulkSave(List<TaxRebatePolicyRequest> requests) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Rebate Policy setup successfully")
                .setPayload(taxRebatePolicyService.bulkSave(requests));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<Envelope> updateMapping(TaxRebatePolicyRequest request) {
        Envelope envelope = new Envelope()
                .setStatus(true)
                .setMessage("Policy update successfully")
                .setPayload(taxRebatePolicyService.updateMapping(request));

        return ResponseEntity.ok(envelope);
    }

    @Override
    public ResponseEntity<?> searchTaxRebate(TaxRebateSearchRequest request) {

        PageResponse<TaxRebatePolicyResponse> result = taxRebatePolicyQueryService.searchRebateInfo(request.getTaxYear(), request.getAmountType(), request.getPageIndex(), request.getPageSize());

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
                        "Tax Rebate fetched successfully",
                        true
                )
        );
    }
}
