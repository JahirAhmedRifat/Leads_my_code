package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementRequest;
import com.leads.microcube.payroll.bonusDisbursement.command.BonusDisbursementSearchRequest;
import com.leads.microcube.payroll.bonusDisbursement.query.BonusDisbursementResponse;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileDetailsRepository;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileQueryService;
import com.leads.microcube.payroll.employeeBonusProfile.EmployeeBonusProfileService;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileRequest;
import com.leads.microcube.payroll.employeeBonusProfile.command.EmployeeBonusProfileSearchRequest;
import com.leads.microcube.payroll.employeeBonusProfile.query.EmployeeBonusProfileResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BonusDisbursementServiceImpl
        extends BaseServiceImpl<BonusDisbursement, BonusDisbursementRequest, BonusDisbursementResponse>
        implements BonusDisbursementService, BonusDisbursementQueryService {

    private final BonusDisbursementRepository disbursementRepository;
    private final BonusDisbursementDetailsRepository disbursementDetailsRepository;
    private final EmployeeBonusProfileDetailsRepository bonusProfileDetailsRepository;


    public BonusDisbursementServiceImpl(
            BaseRepository<BonusDisbursement> repository,
            BonusDisbursementRepository disbursementRepository,
            BonusDisbursementDetailsRepository disbursementDetailsRepository,
            EmployeeBonusProfileDetailsRepository bonusProfileDetailsRepository
    ) {
        super(repository);
        this.disbursementRepository = disbursementRepository;
        this.disbursementDetailsRepository = disbursementDetailsRepository;
        this.bonusProfileDetailsRepository = bonusProfileDetailsRepository;
    }

    @Override
    @Transactional
    public List<BonusDisbursementResponse> bulkSave(List<BonusDisbursementRequest> requestList) {

        if (requestList == null || requestList.isEmpty()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "No data provided"
            );
        }

        List<BonusDisbursement> entitiesToSave = new ArrayList<>();

        for (BonusDisbursementRequest request : requestList) {

            if (request.getBonusDisbursementDetails() == null ||
                    request.getBonusDisbursementDetails().isEmpty()) {

                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "At least one bonus detail is required"
                );
            }

            // Create master entity
            BonusDisbursement entity = new BonusDisbursement();

            BeanUtils.copyProperties(
                    request,
                    entity,
                    "bonusDisbursementDetails", "id", "uuid", "isDeleted", "totalAmount", "disbursementDate"
            );

            // Backend generated Current date
            entity.setDisbursementDate(LocalDate.now());

            // Map child entities
            List<BonusDisbursementDetails> details =
                    request.getBonusDisbursementDetails().stream()
                            .map(d -> {
                                BonusDisbursementDetails detail = new BonusDisbursementDetails();
                                BeanUtils.copyProperties(d, detail, "id", "uuid", "isDeleted");

                                detail.setStatus("DRAFT");
                                detail.setBonusDisbursement(entity);

                                return detail;
                            })
                            .toList();

            // Calculate total amount
            BigDecimal totalAmount = details.stream()
                    .map(BonusDisbursementDetails::getBonusAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            entity.setTotalAmount(totalAmount);
            entity.setBonusDisbursementDetails(details);

            entitiesToSave.add(entity);
        }

        // Bulk save
        List<BonusDisbursement> savedEntities = disbursementRepository.saveAll(entitiesToSave);

        // Step 2: Collect all employee IDs from saved disbursement details
        List<String> employeeIds = savedEntities.stream()
                .flatMap(e -> e.getBonusDisbursementDetails().stream())
                .map(BonusDisbursementDetails::getEmployeeId)
                .distinct()
                .toList();

        // Step 3: Update EmployeeBonusProfileDetails status
        if (!employeeIds.isEmpty()) {
            bonusProfileDetailsRepository.updateStatusByEmployeeIds(
                    employeeIds,
                    "DISBURSED"
            );
        }

        // Convert to response
        return savedEntities.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<BonusDisbursementResponse> searchCriteria(BonusDisbursementSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(), Sort.by("id").descending());

        Page<BonusDisbursement> res =
                disbursementRepository.searchCriteria(
                        request.getEmpOrgId(),
                        request.getEmpBranchId(),
                        request.getBonusYear(),
                        request.getBonusType(),
                        request.getBonusDate(),
                        pageable
                );

        Page<BonusDisbursementResponse> responsePage =
                res.map(
                        entity ->
                                this.convertToTarget(entity, BonusDisbursementResponse.class)
                );

        return toPageResponse(responsePage);
    }

}
