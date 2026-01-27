package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxAcknowledgementRepository extends BaseRepository<TaxAcknowledgement> {

    @Query("""
            SELECT v FROM TaxAcknowledgement v
            WHERE v.isDeleted = false
              AND (:employeeId IS NULL OR LOWER(TRIM(v.employeeId)) LIKE LOWER(CONCAT('%', TRIM(:employeeId), '%')))
              AND (:taxYear IS NULL OR v.taxYear = :taxYear)
            """)
    Page<TaxAcknowledgement> searchCriteria(
            @Param("employeeId") String employeeId,
            @Param("taxYear") Integer taxYear,
            Pageable pageable
    );
}
