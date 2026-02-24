package com.leads.microcube.payroll.organizationBankInformation;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.client.CentrinoClient;
import com.leads.microcube.infrastructure.client.query.CompanyInfoDTO;
import com.leads.microcube.infrastructure.client.query.CompanyOfficeDTO;
import com.leads.microcube.infrastructure.client.query.CurrencyDTO;
import com.leads.microcube.payroll.employeeHeldupSetup.query.EmployeeHeldupResponse;
import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInfoSearchRequest;
import com.leads.microcube.payroll.organizationBankInformation.command.OrganizationBankInformationRequest;
import com.leads.microcube.payroll.organizationBankInformation.query.OrganizationBankInformationResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class OrganizationBankInformationServiceImpl
        extends BaseServiceImpl<OrganizationBankInformation, OrganizationBankInformationRequest, OrganizationBankInformationResponse>
        implements OrganizationBankInformationService, OrganizationBankInformationQueryService {

    private final OrganizationBankInformationRepository bankInformationRepository;
    private final CentrinoClient centrinoClient;

    public OrganizationBankInformationServiceImpl(
            BaseRepository<OrganizationBankInformation> repository,
            OrganizationBankInformationRepository bankInformationRepository,
            CentrinoClient centrinoClient
    ) {
        super(repository);
        this.bankInformationRepository = bankInformationRepository;
        this.centrinoClient = centrinoClient;
    }


    @Override
    @Transactional
    public OrganizationBankInformationResponse create(OrganizationBankInformationRequest request) {

        boolean exists = bankInformationRepository
                .existsForOrgAndBranch(
                        request.getCompanyId(),
                        request.getBranchId(),
                        request.getBankName(),
                        request.getBankBranch(),
                        request.getAccountNumber().trim()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "Bank account already exists for this organization and branch"
            );
        }

        OrganizationBankInformation entity = new OrganizationBankInformation();
        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted"
        );

        entity.setAccountNumber(request.getAccountNumber().trim());
        entity.setRoutingNumber(request.getRoutingNumber().trim());
        entity.setGlAccountNo(request.getGlAccountNo().trim());
        entity.setStatus(Status.valueOf(request.getStatus()));

        OrganizationBankInformation savedEntity = bankInformationRepository.save(entity);

        // Fetch Company list
        List<CompanyInfoDTO> companyList =
                centrinoClient.getCompanyListFromCentrino().block();

        // Fetch branch list
        List<CompanyOfficeDTO> officeList =
                centrinoClient
                        .getCompanyOfficeList(savedEntity.getCompanyId())
                        .block();

        // Fetch currency list
        List<CurrencyDTO> currencyList =
                centrinoClient
                        .getCurrencyList().block();

        // Fetch company name
        String companyName = companyList == null ? null :
                companyList.stream()
                        .filter(c -> savedEntity.getCompanyId().equals(c.getCompanyId()))
                        .map(CompanyInfoDTO::getCompanyName)
                        .findFirst()
                        .orElse(null);

        // Fetch branch name
        String officeName = officeList == null ? null :
                officeList.stream()
                        .filter(o -> savedEntity.getBranchId().equals(o.getOfficeId()))
                        .map(CompanyOfficeDTO::getOfficeName)
                        .findFirst()
                        .orElse(null);

        // Fetch currency name
        String currencyName = currencyList == null ? null :
                currencyList.stream()
                        .filter(o -> savedEntity.getCurrency().equals(o.getCurrencyId()))
                        .map(CurrencyDTO::getCurrencyName)
                        .findFirst()
                        .orElse(null);

        OrganizationBankInformationResponse response = new OrganizationBankInformationResponse();
        BeanUtils.copyProperties(savedEntity, response);
        response.setBankNm(companyName);
        response.setBankBranchNm(officeName);
        response.setCurrencyNm(currencyName);
        response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);

        return response;
    }


    @Override
    @Transactional
    public OrganizationBankInformationResponse update(OrganizationBankInformationRequest request) {

        if (request == null || request.getUuid() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "UUID is required for update"
            );
        }

        OrganizationBankInformation entity = bankInformationRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        // CHECK DUPLICATE (exclude current record)
        boolean exists = bankInformationRepository
                .existsForOrgAndBranchExcludingUuid(
                        request.getCompanyId(),
                        request.getBranchId(),
                        request.getBankName(),
                        request.getBankBranch(),
                        request.getAccountNumber().trim(),
                        request.getUuid()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "Bank account already exists for this organization and branch"
            );
        }


        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted"
        );

        entity.setAccountNumber(request.getAccountNumber().trim());
        entity.setRoutingNumber(request.getRoutingNumber().trim());
        entity.setGlAccountNo(request.getGlAccountNo().trim());
        entity.setStatus(Status.valueOf(request.getStatus()));

        OrganizationBankInformation savedEntity = bankInformationRepository.save(entity);

        // Fetch Company list
        List<CompanyInfoDTO> companyList =
                centrinoClient.getCompanyListFromCentrino().block();

        // Fetch branch list
        List<CompanyOfficeDTO> officeList =
                centrinoClient
                        .getCompanyOfficeList(savedEntity.getCompanyId())
                        .block();

        // Fetch currency list
        List<CurrencyDTO> currencyList =
                centrinoClient
                        .getCurrencyList().block();

        // Fetch company name
        String companyName = companyList == null ? null :
                companyList.stream()
                        .filter(c -> savedEntity.getCompanyId().equals(c.getCompanyId()))
                        .map(CompanyInfoDTO::getCompanyName)
                        .findFirst()
                        .orElse(null);

        // Fetch branch name
        String officeName = officeList == null ? null :
                officeList.stream()
                        .filter(o -> savedEntity.getBranchId().equals(o.getOfficeId()))
                        .map(CompanyOfficeDTO::getOfficeName)
                        .findFirst()
                        .orElse(null);

        // Fetch currency name
        String currencyName = currencyList == null ? null :
                currencyList.stream()
                        .filter(o -> savedEntity.getCurrency().equals(o.getCurrencyId()))
                        .map(CurrencyDTO::getCurrencyName)
                        .findFirst()
                        .orElse(null);

        OrganizationBankInformationResponse response = new OrganizationBankInformationResponse();
        BeanUtils.copyProperties(savedEntity, response);
        response.setBankNm(companyName);
        response.setBankBranchNm(officeName);
        response.setCurrencyNm(currencyName);
        response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);

        return response;
    }

    @Override
    public PageResponse<OrganizationBankInformationResponse> searchCriteria(
            OrganizationBankInfoSearchRequest request
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
        Page<OrganizationBankInformation> result = bankInformationRepository.searchCriteria(
                request.getCompanyId(),
                request.getBranchId(),
                request.getBankName(),
                request.getBankBranch(),
                pageable
        );

        // --- Convert to response ---
        Page<OrganizationBankInformationResponse> responsePage = result.map(entity -> {

            // Fetch Company list
            List<CompanyInfoDTO> companyList =
                    centrinoClient.getCompanyListFromCentrino().block();

            // Fetch branch list
            List<CompanyOfficeDTO> officeList =
                    centrinoClient
                            .getCompanyOfficeList(entity.getCompanyId())
                            .block();

            // Fetch currency list
            List<CurrencyDTO> currencyList =
                    centrinoClient
                            .getCurrencyList().block();

            // Fetch company name
            String companyName = companyList == null ? null :
                    companyList.stream()
                            .filter(c -> entity.getCompanyId().equals(c.getCompanyId()))
                            .map(CompanyInfoDTO::getCompanyName)
                            .findFirst()
                            .orElse(null);

            // Fetch branch name
            String officeName = officeList == null ? null :
                    officeList.stream()
                            .filter(o -> entity.getBranchId().equals(o.getOfficeId()))
                            .map(CompanyOfficeDTO::getOfficeName)
                            .findFirst()
                            .orElse(null);

            // Fetch currency name
            String currencyName = currencyList == null ? null :
                    currencyList.stream()
                            .filter(o -> entity.getCurrency().equals(o.getCurrencyId()))
                            .map(CurrencyDTO::getCurrencyName)
                            .findFirst()
                            .orElse(null);

            OrganizationBankInformationResponse response = new OrganizationBankInformationResponse();
            BeanUtils.copyProperties(entity, response);
            response.setBankNm(companyName);
            response.setBankBranchNm(officeName);
            response.setCurrencyNm(currencyName);
            response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
            return response;
        });

        return toPageResponse(responsePage);
    }
}
