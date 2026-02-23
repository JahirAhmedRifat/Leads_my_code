package com.leads.microcube.payroll.employeeBonusProfile;

import com.leads.microcube.base.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeBonusProfileDetailsRepository extends BaseRepository<EmployeeBonusProfileDetails> {
    @Modifying
    @Query("""
                   UPDATE EmployeeBonusProfileDetails d
                   SET d.status = :newStatus
                   WHERE d.employeeId IN :employeeIds
                     AND d.status = 'DRAFT'
                     AND d.isDeleted = false
            """)
    int updateStatusByEmployeeIds(
            @Param("employeeIds") List<String> employeeIds,
            @Param("newStatus") String newStatus
    );
}
