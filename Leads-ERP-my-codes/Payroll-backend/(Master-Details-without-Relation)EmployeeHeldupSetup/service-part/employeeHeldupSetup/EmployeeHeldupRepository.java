package com.leads.microcube.payroll.employeeHeldupSetup;


import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeHeldupRepository extends BaseRepository<EmployeeHeldup> {

    // ------------- check before save -------------------
    @Query("""
                SELECT COUNT(e) > 0
                FROM EmployeeHeldup e
                WHERE e.isDeleted = false
                  AND e.employeeId = :employeeId
                  AND e.heldupType = :heldupType
                  AND (
                        :fromDate <= e.toDate
                    AND :toDate   >= e.fromDate
                  )
            """)
    boolean existsOverlappingHeldup(
            String employeeId,
            HeldupType heldupType,
            LocalDate fromDate,
            LocalDate toDate
    );

    // ------------- check before update -------------------
    @Query("""
                SELECT COUNT(e) > 0
                FROM EmployeeHeldup e
                WHERE e.isDeleted = false
                  AND e.employeeId = :employeeId
                  AND e.heldupType = :heldupType
                  AND e.uuid <> :uuid
                  AND (
                        :fromDate <= e.toDate
                    AND :toDate   >= e.fromDate
                  )
            """)
    boolean existsOverlappingHeldupExcludeSelf(
            String employeeId,
            HeldupType heldupType,
            String uuid,
            LocalDate fromDate,
            LocalDate toDate
    );

    //------------ search criteria -------------------------
    @Query("""
                SELECT v FROM EmployeeHeldup v
                WHERE v.isDeleted = false
                  AND (:employeeId IS NULL OR LOWER(TRIM(v.employeeId)) LIKE LOWER(CONCAT('%', TRIM(:employeeId), '%')))
                  AND (:heldupType IS NULL OR v.heldupType = :heldupType)
                  AND (:fromDate IS NULL OR v.toDate >= :fromDate)
                  AND (:toDate IS NULL OR v.fromDate <= :toDate)
            """)
    Page<EmployeeHeldup> searchCriteria(
            @Param("employeeId") String employeeId,
            @Param("heldupType") HeldupType heldupType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );


    @Query("""
    SELECT eh
    FROM EmployeeHeldup eh
    WHERE eh.employeeId in (:employeeIds)
      AND eh.heldupType = :heldupType
      AND :checkDate BETWEEN eh.fromDate AND eh.toDate
    """)
    List<EmployeeHeldup> findHeldupByEmployeesAndDate(
            @Param("employeeIds") Set<String> employeeIds,
            @Param("heldupType") String heldupType,
            @Param("checkDate") LocalDate checkDate
    );


}