package com.leads.microcube.payroll.organizationCashInformation;

import com.leads.microcube.Enum.Status;
import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInfoSearchRequest;
import com.leads.microcube.payroll.organizationCashInformation.command.OrganizationCashInformationRequest;
import com.leads.microcube.payroll.organizationCashInformation.query.OrganizationCashInformationResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrganizationCashInformationServiceImpl
        extends BaseServiceImpl<OrganizationCashInformation, OrganizationCashInformationRequest, OrganizationCashInformationResponse>
        implements OrganizationCashInformationService, OrganizationCashInformationQueryService {

    private final OrganizationCashInformationRepository cashInformationRepository;

    public OrganizationCashInformationServiceImpl(
            BaseRepository<OrganizationCashInformation> repository,
            OrganizationCashInformationRepository cashInformationRepository
    ) {
        super(repository);
        this.cashInformationRepository = cashInformationRepository;
    }

    @Override
    @Transactional
    public OrganizationCashInformationResponse create(OrganizationCashInformationRequest request) {

        boolean exists = cashInformationRepository
                .existsForOrgAndBranch(
                        request.getCompanyId(),
                        request.getBranchId(),
                        request.getCashRegNumber().trim()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "This Registration number already exists for this organization and branch"
            );
        }

        OrganizationCashInformation entity = new OrganizationCashInformation();
        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted"
        );

        entity.setCashRegNumber(request.getCashRegNumber().trim());
        entity.setStatus(Status.valueOf(request.getStatus()));

        OrganizationCashInformation savedEntity = cashInformationRepository.save(entity);

        return toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrganizationCashInformationResponse update(OrganizationCashInformationRequest request) {

        if (request == null || request.getUuid() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "UUID is required for update"
            );
        }

        OrganizationCashInformation entity = cashInformationRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        // CHECK DUPLICATE (exclude current record)
        boolean exists = cashInformationRepository
                .existsForOrgAndBranchExcludingUuid(
                        request.getCompanyId(),
                        request.getBranchId(),
                        request.getCashRegNumber().trim(),
                        request.getUuid()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "This Registration number already exists for this organization and branch"
            );
        }


        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted"
        );


        entity.setCashRegNumber(request.getCashRegNumber().trim());
        entity.setStatus(Status.valueOf(request.getStatus()));

        OrganizationCashInformation savedEntity = cashInformationRepository.save(entity);

        return toResponse(savedEntity);
    }

    @Override
    public PageResponse<OrganizationCashInformationResponse> searchCriteria(
            OrganizationCashInfoSearchRequest request
    ) {
        if (request == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Search request cannot be null"
            );
        }

        // --- Pagination ---
        Pageable pageable = PageRequest.of(
                request.getPageIndex(),
                request.getPageSize(),
                Sort.by("id").descending()
        );

        // --- Execute search ---
        Page<OrganizationCashInformation> result = cashInformationRepository.searchCriteria(
                request.getCompanyId(),
                request.getBranchId(),
                request.getCashRegName(),
                request.getCashRegNumber(),
                pageable
        );

        // --- Convert to response ---
        Page<OrganizationCashInformationResponse> responsePage = result.map(entity -> {
            OrganizationCashInformationResponse response = new OrganizationCashInformationResponse();
            BeanUtils.copyProperties(entity, response);
            response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
            return response;
        });

        return toPageResponse(responsePage);
    }

}
