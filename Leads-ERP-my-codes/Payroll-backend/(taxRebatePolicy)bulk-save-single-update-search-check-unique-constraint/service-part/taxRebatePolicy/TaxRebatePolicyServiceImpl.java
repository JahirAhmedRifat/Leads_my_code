package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.tax.taxRebatePolicy.command.TaxRebatePolicyRequest;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TaxRebatePolicyServiceImpl
        extends BaseServiceImpl<TaxRebatePolicy, TaxRebatePolicyRequest, TaxRebatePolicyResponse>
        implements TaxRebatePolicyService, TaxRebatePolicyQueryService {

    private final TaxRebatePolicyRepository taxRebatePolicyRepository;

    public TaxRebatePolicyServiceImpl(
            BaseRepository<TaxRebatePolicy> repository,
            TaxRebatePolicyRepository taxRebatePolicyRepository
    ) {
        super(repository);
        this.taxRebatePolicyRepository = taxRebatePolicyRepository;
    }

    @Override
    @Transactional
    public List<TaxRebatePolicyResponse> bulkSave(List<TaxRebatePolicyRequest> requestList) {

        if (requestList == null || requestList.isEmpty()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "No data provided"
            );
        }

        //--- unique constraint setup//CHECK DUPLICATE INSIDE REQUEST ------
        Map<String, List<TaxRebatePolicyRequest>> grouped =
                requestList.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getTaxYear() + "_" + r.getAmountType()
                        ));

        List<String> duplicateKeys = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateKeys.isEmpty()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate amountType found in request for taxYear: " + duplicateKeys
            );
        }

        // ----- now checking from database isExists or not ----
        List<String> exists = requestList.stream()
                .filter(r -> taxRebatePolicyRepository
                        .existsByTaxYearAndAmountTypeAndIsDeletedFalse(
                                r.getTaxYear(),
                                r.getAmountType()
                        ))
                .map(r -> "Amount Type " + r.getAmountType() + " already exists for Tax Year " + r.getTaxYear())
                .toList();

        if (!exists.isEmpty()) {
            // Join all messages into one string
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    String.join("; \n ", exists)
            );
        }
        //------------ for save -------------
        List<TaxRebatePolicy> entityList = requestList.stream()
                .map(request -> {
                    TaxRebatePolicy entity = new TaxRebatePolicy();
                    BeanUtils.copyProperties(request, entity, "id", "uuid", "isDeleted");
                    return entity;
                })
                .collect(Collectors.toList());

        List<TaxRebatePolicy> savedList =
                taxRebatePolicyRepository.saveAll(entityList);

        return mapToResponse(savedList);
    }

    @Override
    @Transactional
    public TaxRebatePolicyResponse updateMapping(
            TaxRebatePolicyRequest request
    ) {

        if (request == null || request.getUuid() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "UUID is required for update"
            );
        }

        TaxRebatePolicy entity = taxRebatePolicyRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        // ---------- BLOCK UPDATE FOR PREVIOUS TAX YEAR ----------
        int currentYear = java.time.Year.now().getValue();

        if (request.getTaxYear() != null && request.getTaxYear() < currentYear) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Update is not allowed for previous Tax Year"
            );
        }

        // taxYear should NOT change during update
        Integer taxYear = entity.getTaxYear();

        // CHECK DUPLICATE (exclude current record)
        boolean exists = taxRebatePolicyRepository
                .existsByTaxYearAndAmountTypeAndIsDeletedFalseAndUuidNot(
                        taxYear,
                        request.getAmountType(),
                        request.getUuid()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "This Amount Type already exists for Tax Year " + taxYear
            );
        }

        // Only update allowed fields
        entity.setPolicySerialNo(request.getPolicySerialNo());
        entity.setAmountType(request.getAmountType());
        entity.setTotalAmount(request.getTotalAmount());

        TaxRebatePolicy saved = taxRebatePolicyRepository.save(entity);
        return toResponse(saved);
    }

    private List<TaxRebatePolicyResponse> mapToResponse(
            List<TaxRebatePolicy> entityList) {

        return entityList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<TaxRebatePolicyResponse> searchRebateInfo(
            Integer taxYear,
            Long amountType,
            int pageIndex,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<TaxRebatePolicy> res = taxRebatePolicyRepository.searchRebateInfo(taxYear, amountType, pageable);

        Page<TaxRebatePolicyResponse> responsePage = res.map(TaxRebatePolicy -> this.convertToTarget(TaxRebatePolicy, TaxRebatePolicyResponse.class));
        return toPageResponse(responsePage);
    }

}
