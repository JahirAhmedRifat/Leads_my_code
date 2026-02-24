package com.leads.microcube.payroll.organizationCashInformation;


import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationCashInformationRepository extends BaseRepository<OrganizationCashInformation> {

    //--------- check before save -------------------
    @Query("SELECT COUNT(e) > 0 FROM OrganizationCashInformation e WHERE " +
            "e.companyId = :companyId AND e.branchId = :branchId AND " +
            "e.cashRegNumber = :cashRegNumber AND e.isDeleted = false")
    boolean existsForOrgAndBranch(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("cashRegNumber") String cashRegNumber
    );

    //--------- check before update -------------------
    @Query("SELECT COUNT(e) > 0 FROM OrganizationCashInformation e WHERE " +
            "e.companyId = :companyId AND e.branchId = :branchId AND " +
            "e.cashRegNumber = :cashRegNumber AND e.isDeleted = false AND e.uuid != :uuid")
    boolean existsForOrgAndBranchExcludingUuid(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("cashRegNumber") String cashRegNumber,
            @Param("uuid") String uuid
    );

    //--------- Search Criteria -------------------
    @Query("""
                SELECT v FROM OrganizationCashInformation v
                WHERE v.isDeleted = false
                  AND (:companyId IS NULL OR v.companyId = :companyId)
                  AND (:branchId IS NULL OR v.branchId = :branchId)
                  AND (:cashRegName IS NULL OR LOWER(TRIM(v.cashRegName)) LIKE LOWER(CONCAT('%', TRIM(:cashRegName), '%')))
                  AND (:cashRegNumber IS NULL OR LOWER(TRIM(v.cashRegNumber)) LIKE LOWER(CONCAT('%', TRIM(:cashRegNumber), '%')))
            """)
    Page<OrganizationCashInformation> searchCriteria(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("cashRegName") String cashRegName,
            @Param("cashRegNumber") String cashRegNumber,
            Pageable pageable
    );

}