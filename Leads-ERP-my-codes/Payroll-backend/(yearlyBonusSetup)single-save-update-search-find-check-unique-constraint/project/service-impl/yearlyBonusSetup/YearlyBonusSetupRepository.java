package com.leads.microcube.payroll.yearlyBonusSetup;


import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.tax.taxRebatePolicy.TaxRebatePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface YearlyBonusSetupRepository extends BaseRepository<YearlyBonusSetup> {

    // ------------- check for save -------------------
    boolean existsByBonusYearAndBonusTypeAndBonusPolicyAndIsDeletedFalse(
            Integer bonusYear,
            String bonusType,
            Long bonusPolicy
    );

    // ------------- check for update -------------------
    boolean existsByBonusYearAndBonusTypeAndBonusPolicyAndIsDeletedFalseAndUuidNot(
            Integer bonusYear,
            String bonusType,
            Long bonusPolicy,
            String uuid
    );

    @Query("""
            SELECT v FROM YearlyBonusSetup v
            WHERE v.isDeleted = false
              AND (:bonusYear IS NULL OR v.bonusYear = :bonusYear)
              AND (:bonusPolicy IS NULL OR v.bonusPolicy = :bonusPolicy)
              AND (:bonusType IS NULL OR v.bonusType = :bonusType)
            """)
    Page<YearlyBonusSetup> searchCriteria(
            @Param("bonusYear") Integer bonusYear,
            @Param("bonusPolicy") Long bonusPolicy,
            @Param("bonusType") String bonusType,
            Pageable pageable
    );

    Optional<YearlyBonusSetup> findByBonusYearAndBonusPolicyAndBonusTypeAndIsDeletedFalse(
            Integer bonusYear,
            Long bonusPolicy,
            String bonusType
    );

    Optional<YearlyBonusSetup> findByBonusYearAndBonusTypeAndIsDeletedFalse(
            Integer bonusYear,
            String bonusType
    );

}