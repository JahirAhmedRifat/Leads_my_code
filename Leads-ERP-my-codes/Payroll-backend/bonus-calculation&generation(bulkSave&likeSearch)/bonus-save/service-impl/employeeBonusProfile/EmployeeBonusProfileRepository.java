package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeBonusProfileRepository extends BaseRepository<EmployeeBonusProfile> {

    // ------ check for bulk save -------
    boolean existsByEmployeeIdAndYearlyBonusUuidAndIsDeletedFalse(
            String employeeId,
            String yearlyBonusUuid
    );

    @Query("""
            SELECT v FROM EmployeeBonusProfile v
            WHERE v.isDeleted = false
              AND (:employeeId IS NULL OR LOWER(TRIM(v.employeeId)) LIKE LOWER(CONCAT('%', TRIM(:employeeId), '%')))
              AND (:department IS NULL OR v.department = :department)
              AND (:designation IS NULL OR v.designation = :designation)
              AND (:bonusYear IS NULL OR v.bonusYear = :bonusYear)
            """)
    Page<EmployeeBonusProfile> searchCriteria(
            @Param("employeeId") String employeeId,
            @Param("department") Long department,
            @Param("designation") Long designation,
            @Param("bonusYear") Integer bonusYear,
            Pageable pageable
    );

}
