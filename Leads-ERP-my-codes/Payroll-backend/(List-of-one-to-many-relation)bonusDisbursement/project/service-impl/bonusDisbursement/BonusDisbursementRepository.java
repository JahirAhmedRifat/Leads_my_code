package com.leads.microcube.payroll.bonusDisbursement;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface BonusDisbursementRepository extends BaseRepository<BonusDisbursement> {

    @Query("""
            SELECT v FROM BonusDisbursement v
            WHERE v.isDeleted = false
              AND (:empOrgId IS NULL OR v.empOrgId = :empOrgId)
              AND (:empBranchId IS NULL OR v.empBranchId = :empBranchId)
              AND (:bonusYear IS NULL OR v.bonusYear = :bonusYear)
              AND (:bonusType IS NULL OR v.bonusType = :bonusType)
              AND (:bonusDate IS NULL OR v.bonusDate = :bonusDate)
            """)
    Page<BonusDisbursement> searchCriteria(
            @Param("empOrgId") String empOrgId,
            @Param("empBranchId") String empBranchId,
            @Param("bonusYear") Integer bonusYear,
            @Param("bonusType") String bonusType,
            @Param("bonusDate") LocalDate bonusDate,
            Pageable pageable
    );

}
