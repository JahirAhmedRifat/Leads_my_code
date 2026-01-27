package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeIdAndTaxYearRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.command.EmployeeWiseZoneAndCircleMappingRequest;
import com.leads.microcube.tax.employeeWiseZoneAndCircleMapping.query.EmployeeWiseZoneAndCircleMappingResponse;
import com.leads.microcube.tax.taxRebatePolicy.TaxRebatePolicy;
import com.leads.microcube.tax.taxRebatePolicy.query.TaxRebatePolicyResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
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
public class EmployeeWiseZoneAndCircleMappingServiceImpl
        extends BaseServiceImpl<EmployeeWiseZoneAndCircleMapping, EmployeeWiseZoneAndCircleMappingRequest, EmployeeWiseZoneAndCircleMappingResponse>
        implements EmployeeWiseZoneAndCircleMappingService, EmployeeWiseZoneAndCircleMappingQueryService {

    private final EmployeeWiseZoneAndCircleMappingRepository mappingRepository;

    public EmployeeWiseZoneAndCircleMappingServiceImpl(
            BaseRepository<EmployeeWiseZoneAndCircleMapping> repository,
            EmployeeWiseZoneAndCircleMappingRepository mappingRepository
    ) {
        super(repository);
        this.mappingRepository = mappingRepository;
    }

    @Override
    @Transactional
    public List<EmployeeWiseZoneAndCircleMappingResponse> bulkSave(List<EmployeeWiseZoneAndCircleMappingRequest> requestList) {

        if (requestList == null || requestList.isEmpty()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Employee-wise zone & circle mapping list cannot be empty"
            );
        }

        //--- unique constraint setup//CHECK DUPLICATE INSIDE REQUEST ------
        Map<String, List<EmployeeWiseZoneAndCircleMappingRequest>> grouped =
                requestList.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getTaxYear() + "_" + r.getEmployeeId()
                        ));

        List<String> duplicateKeys = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateKeys.isEmpty()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate employeeId found in request for taxYear: " + duplicateKeys
            );
        }

        // ----- now checking from database isExists or not ----
        List<String> existingEmployees = requestList.stream()
                .filter(r -> mappingRepository
                        .existsByTaxYearAndEmployeeIdAndIsDeletedFalse(
                                r.getTaxYear(),
                                r.getEmployeeId()
                        ))
                .map(r -> "Employee ID " + r.getEmployeeId() + " already exists for Tax Year " + r.getTaxYear())
                .toList();

        if (!existingEmployees.isEmpty()) {
            // Join all messages into one string
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    String.join("; \n ", existingEmployees)
            );
        }
        //------------ for save -------------
        List<EmployeeWiseZoneAndCircleMapping> entityList = requestList.stream()
                .map(request -> {
                    EmployeeWiseZoneAndCircleMapping entity = new EmployeeWiseZoneAndCircleMapping();
                    BeanUtils.copyProperties(request, entity, "id", "uuid", "isDeleted");
                    return entity;
                })
                .collect(Collectors.toList());

        List<EmployeeWiseZoneAndCircleMapping> savedList =
                mappingRepository.saveAll(entityList);

        return mapToResponse(savedList);
    }

    @Override
    @Transactional
    public EmployeeWiseZoneAndCircleMappingResponse updateMapping(
            EmployeeWiseZoneAndCircleMappingRequest request
    ) {

        EmployeeWiseZoneAndCircleMapping entity = mappingRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Employee-wise mapping not found"
                ));

        // Only update allowed fields
        entity.setTaxZone(request.getTaxZone());
        entity.setTaxCircle(request.getTaxCircle());

        EmployeeWiseZoneAndCircleMapping saved =
                mappingRepository.save(entity);

        return toResponse(saved);
    }

    private List<EmployeeWiseZoneAndCircleMappingResponse> mapToResponse(
            List<EmployeeWiseZoneAndCircleMapping> entityList) {

        return entityList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeWiseZoneAndCircleMappingResponse getDataByEmployeeIdAndTaxYear(
            EmployeeIdAndTaxYearRequest request
    ) {
        EmployeeWiseZoneAndCircleMapping entity = mappingRepository
                .findByEmployeeIdAndTaxYearAndIsDeletedFalse(request.getEmployeeId(), request.getTaxYear())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));
        return toResponse(entity);
    }

    @Override
    public PageResponse<EmployeeWiseZoneAndCircleMappingResponse> searchCriteria(
            Integer taxYear,
            String employeeId,
            Long taxZone,
            Long taxCircle,
            int pageIndex,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<EmployeeWiseZoneAndCircleMapping> res = mappingRepository.searchCriteria(
                taxYear,
                employeeId,
                taxZone,
                taxCircle,
                pageable
        );

        Page<EmployeeWiseZoneAndCircleMappingResponse> responsePage = res.map(
                EmployeeWiseZoneAndCircleMapping ->
                        this.convertToTarget(EmployeeWiseZoneAndCircleMapping, EmployeeWiseZoneAndCircleMappingResponse.class)
        );
        return toPageResponse(responsePage);
    }

}
