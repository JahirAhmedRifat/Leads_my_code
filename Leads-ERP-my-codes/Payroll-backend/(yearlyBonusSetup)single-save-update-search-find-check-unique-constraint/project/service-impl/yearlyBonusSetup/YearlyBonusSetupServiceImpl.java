package com.leads.microcube.payroll.yearlyBonusSetup;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.yearlyBonusSetup.command.YearlyBonusSetupRequest;
import com.leads.microcube.payroll.yearlyBonusSetup.query.YearlyBonusSetupResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class YearlyBonusSetupServiceImpl
        extends BaseServiceImpl<YearlyBonusSetup, YearlyBonusSetupRequest, YearlyBonusSetupResponse>
        implements YearlyBonusSetupService, YearlyBonusSetupQueryService {

    private final YearlyBonusSetupRepository bonusSetupRepository;

    public YearlyBonusSetupServiceImpl(
            BaseRepository<YearlyBonusSetup> repository,
            YearlyBonusSetupRepository bonusSetupRepository
    ) {
        super(repository);
        this.bonusSetupRepository = bonusSetupRepository;
    }

    @Override
    @Transactional
    public YearlyBonusSetupResponse create(YearlyBonusSetupRequest request) {

        if (request == null || request.getBonusYear() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Bonus year is required"
            );
        }

        boolean exists = bonusSetupRepository
                .existsByBonusYearAndBonusTypeAndBonusPolicyAndIsDeletedFalse(
                        request.getBonusYear(),
                        request.getBonusType(),
                        request.getBonusPolicy()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "Bonus setup already exists for this bonus year, bonus type and bonus policy"
            );
        }

        YearlyBonusSetup entity = new YearlyBonusSetup();
        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted"
        );

        YearlyBonusSetup savedEntity = bonusSetupRepository.save(entity);

        return toResponse(savedEntity);
    }

    @Override
    @Transactional
    public YearlyBonusSetupResponse update(YearlyBonusSetupRequest request) {

        if (request == null || request.getUuid() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "UUID is required for update"
            );
        }

        YearlyBonusSetup entity = bonusSetupRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        // CHECK DUPLICATE (exclude current record)
        boolean exists = bonusSetupRepository
                .existsByBonusYearAndBonusTypeAndBonusPolicyAndIsDeletedFalseAndUuidNot(
                        entity.getBonusYear(),
                        request.getBonusType(),
                        request.getBonusPolicy(),
                        request.getUuid()
                );

        if (exists) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "Bonus setup already exists for this bonus year, bonus type and bonus policy"
            );
        }

        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted", "bonusYear"
        );

        YearlyBonusSetup savedEntity = bonusSetupRepository.save(entity);

        return toResponse(savedEntity);
    }

    @Override
    public PageResponse<YearlyBonusSetupResponse> searchCriteria(
            Integer bonusYear,
            Long bonusPolicy,
            String bonusType,
            int pageIndex,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<YearlyBonusSetup> res = bonusSetupRepository.searchCriteria(
                bonusYear,
                bonusPolicy,
                bonusType,
                pageable
        );

        Page<YearlyBonusSetupResponse> responsePage = res.map(
                YearlyBonusSetup ->
                        this.convertToTarget(YearlyBonusSetup, YearlyBonusSetupResponse.class)
        );
        return toPageResponse(responsePage);
    }

    @Override
    public YearlyBonusSetupResponse findByBonusYearAndPolicyAndType(
            Integer bonusYear,
            Long bonusPolicy,
            String bonusType
    ) {
        YearlyBonusSetup entity = bonusSetupRepository
                .findByBonusYearAndBonusPolicyAndBonusTypeAndIsDeletedFalse(
                        bonusYear,
                        bonusPolicy,
                        bonusType
                ).orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        return toResponse(entity);
    }

}
