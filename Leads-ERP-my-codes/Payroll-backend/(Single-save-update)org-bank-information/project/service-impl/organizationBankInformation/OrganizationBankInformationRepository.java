package com.leads.microcube.payroll.organizationBankInformation;


import com.leads.microcube.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationBankInformationRepository extends BaseRepository<OrganizationBankInformation> {

    //--------- check before save -------------------
    @Query("SELECT COUNT(e) > 0 FROM OrganizationBankInformation e WHERE " +
            "e.companyId = :companyId AND e.branchId = :branchId AND " +
            "e.bankName = :bankName AND e.bankBranch = :bankBranch AND " +
            "e.accountNumber = :accountNumber AND e.isDeleted = false")
    boolean existsForOrgAndBranch(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("bankName") Long bankName,
            @Param("bankBranch") Long bankBranch,
            @Param("accountNumber") String accountNumber
    );

    //--------- check before update -------------------
    @Query("SELECT COUNT(e) > 0 FROM OrganizationBankInformation e WHERE " +
            "e.companyId = :companyId AND e.branchId = :branchId AND " +
            "e.bankName = :bankName AND e.bankBranch = :bankBranch AND " +
            "e.accountNumber = :accountNumber AND e.isDeleted = false AND e.uuid != :uuid")
    boolean existsForOrgAndBranchExcludingUuid(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("bankName") Long bankName,
            @Param("bankBranch") Long bankBranch,
            @Param("accountNumber") String accountNumber,
            @Param("uuid") String uuid
    );

    //--------- Search Criteria -------------------
    @Query("""
                SELECT v FROM OrganizationBankInformation v
                WHERE v.isDeleted = false
                  AND (:companyId IS NULL OR v.companyId = :companyId)
                  AND (:branchId IS NULL OR v.branchId = :branchId)
                  AND (:bankName IS NULL OR v.bankName = :bankName)
                  AND (:bankBranch IS NULL OR v.bankBranch = :bankBranch)
            """)
    Page<OrganizationBankInformation> searchCriteria(
            @Param("companyId") Long companyId,
            @Param("branchId") Long branchId,
            @Param("bankName") Long bankName,
            @Param("bankBranch") Long bankBranch,
            Pageable pageable
    );

    Optional<OrganizationBankInformation> findFirstById(Long aLong);
}