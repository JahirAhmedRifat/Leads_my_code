package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
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
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeBonusProfileServiceImpl
        extends BaseServiceImpl<EmployeeBonusProfile, EmployeeBonusProfileRequest, EmployeeBonusProfileResponse>
        implements EmployeeBonusProfileService, EmployeeBonusProfileQueryService {

    private final EmployeeBonusProfileRepository bonusProfileRepository;
    private final EmployeeBonusProfileDetailsRepository bonusProfileDetailsRepository;

    public EmployeeBonusProfileServiceImpl(
            BaseRepository<EmployeeBonusProfile> repository,
            EmployeeBonusProfileRepository bonusProfileRepository,
            EmployeeBonusProfileDetailsRepository bonusProfileDetailsRepository
    ) {
        super(repository);
        this.bonusProfileRepository = bonusProfileRepository;
        this.bonusProfileDetailsRepository = bonusProfileDetailsRepository;
    }

//    @Transactional
//    public List<EmployeeBonusProfileResponse> bulkSave(List<EmployeeBonusProfileRequest> requestList) {
//
//        if (requestList == null || requestList.isEmpty()) {
//            throw new CustomException(
//                    HttpStatus.BAD_REQUEST,
//                    "No data provided"
//            );
//        }
//
//        // ----- checking from database isExists or not ----
//        boolean exists = requestList.stream()
//                .anyMatch(r ->
//                        bonusProfileRepository
//                                .existsByEmployeeIdAndYearlyBonusUuidAndIsDeletedFalse(
//                                        r.getEmployeeId(),
//                                        r.getYearlyBonusUuid()
//                                )
//                );
//
//        if (exists) {
//            throw new CustomException(
//                    HttpStatus.CONFLICT,
//                    "Employee bonus profile already exists for this yearly bonus"
//            );
//        }
//
//        //------------ for save -------------
//        List<EmployeeBonusProfile> entityList = requestList.stream()
//                .map(request -> {
//                    EmployeeBonusProfile entity = new EmployeeBonusProfile();
//                    BeanUtils.copyProperties(request, entity, "id", "uuid", "isDeleted");
//                    entity.setStatus("Draft");
//                    return entity;
//                })
//                .collect(Collectors.toList());
//
//        List<EmployeeBonusProfile> savedList =
//                bonusProfileRepository.saveAll(entityList);
//
//        return toResponseList(savedList);
//    }

    @Override
    @Transactional
    public EmployeeBonusProfileResponse bonusCreate(EmployeeBonusProfileRequest request) {

        if (request == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Request cannot be null"
            );
        }

        if (
                request.getBonusProfileDetails() == null ||
                        request.getBonusProfileDetails().isEmpty()
        ) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "At least one bonus profile detail is required"
            );
        }

        boolean exists = bonusProfileRepository
                .existsByEmpOrgIdAndBonusYearAndBonusTypeAndIsDeletedFalse(
                        request.getEmpOrgId(),
                        request.getBonusYear(),
                        request.getBonusType()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Bonus setup already exists for this selected organization"
            );
        }

        // Create parent entity
        EmployeeBonusProfile entity = new EmployeeBonusProfile();
        BeanUtils.copyProperties(
                request,
                entity,
                "bonusProfileDetails", "id", "uuid", "isDeleted"
        );

        // Map child entities
        List<EmployeeBonusProfileDetails> details =
                request.getBonusProfileDetails().stream()
                        .map(d -> {
                            EmployeeBonusProfileDetails detail = new EmployeeBonusProfileDetails();
                            BeanUtils.copyProperties(d, detail, "id", "uuid", "isDeleted");
                            detail.setStatus("DRAFT");
                            detail.setEmployeeBonusProfile(entity);
                            return detail;
                        })
                        .toList();

        // Calculate total amount
        BigDecimal totalAmount =
                details.stream()
                        .map(EmployeeBonusProfileDetails::getBonusAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Set relations + total
        entity.setBonusProfileDetails(details);
        entity.setTotalAmount(totalAmount);

        EmployeeBonusProfile saved = bonusProfileRepository.save(entity);

        return toResponse(saved);

    }


    @Override
    public PageResponse<EmployeeBonusProfileResponse> searchCriteria(EmployeeBonusProfileSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(), Sort.by("id").descending());

        Page<EmployeeBonusProfile> res =
                bonusProfileRepository.searchCriteria(
                        request.getEmpOrgId(),
                        request.getEmpBranchId(),
                        request.getBonusYear(),
                        request.getBonusType(),
                        request.getBonusDate(),
                        pageable
                );

        Page<EmployeeBonusProfileResponse> responsePage =
                res.map(
                        entity ->
                                this.convertToTarget(entity, EmployeeBonusProfileResponse.class)
                );

        return toPageResponse(responsePage);

    }

}
