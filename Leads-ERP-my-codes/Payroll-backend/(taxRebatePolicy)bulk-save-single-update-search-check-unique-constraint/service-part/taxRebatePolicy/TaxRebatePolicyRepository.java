package com.leads.microcube.tax.taxRebatePolicy;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRebatePolicyRepository extends BaseRepository<TaxRebatePolicy> {

    // ------ check for bulk save -------
    boolean existsByTaxYearAndAmountTypeAndIsDeletedFalse(
            Integer taxYear,
            Long amountType
    );

    // ------------- check for update -------------------
    boolean existsByTaxYearAndAmountTypeAndIsDeletedFalseAndUuidNot(
            Integer taxYear,
            Long amountType,
            String uuid
    );

    @Query("""
            SELECT v FROM TaxRebatePolicy v
            WHERE v.isDeleted = false
              AND (:taxYear IS NULL OR v.taxYear = :taxYear)
              AND (:amountType IS NULL OR v.amountType = :amountType)
            """)
    Page<TaxRebatePolicy> searchRebateInfo(
            @Param("taxYear") Integer taxYear,
            @Param("amountType") Long amountType,
            Pageable pageable
    );

    // Fetch all rebate policies by tax year and not deleted
    List<TaxRebatePolicy> findAllByTaxYearAndIsDeletedFalse(Integer taxYear);

}
