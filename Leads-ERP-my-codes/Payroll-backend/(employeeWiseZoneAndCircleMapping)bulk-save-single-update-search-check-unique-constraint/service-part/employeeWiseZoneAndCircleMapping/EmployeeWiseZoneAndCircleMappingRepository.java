package com.leads.microcube.tax.employeeWiseZoneAndCircleMapping;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeWiseZoneAndCircleMappingRepository extends BaseRepository<EmployeeWiseZoneAndCircleMapping> {

    boolean existsByTaxYearAndEmployeeIdAndIsDeletedFalse(
            Integer taxYear,
            String employeeId
    );

    Optional<EmployeeWiseZoneAndCircleMapping> findByEmployeeIdAndTaxYearAndIsDeletedFalse(
            String employeeId,
            Integer taxYear
    );

    @Query("""
            SELECT v FROM EmployeeWiseZoneAndCircleMapping v
            WHERE v.isDeleted = false
              AND (:taxYear IS NULL OR v.taxYear = :taxYear)
              AND (:employeeId IS NULL OR LOWER(TRIM(v.employeeId)) LIKE LOWER(CONCAT('%', TRIM(:employeeId), '%')))
              AND (:taxZone IS NULL OR v.taxZone = :taxZone)
              AND (:taxCircle IS NULL OR v.taxCircle = :taxCircle)
            """)
    Page<EmployeeWiseZoneAndCircleMapping> searchCriteria(
            @Param("taxYear") Integer taxYear,
            @Param("employeeId") String employeeId,
            @Param("taxZone") Long taxZone,
            @Param("taxCircle") Long taxCircle,
            Pageable pageable
    );

}
