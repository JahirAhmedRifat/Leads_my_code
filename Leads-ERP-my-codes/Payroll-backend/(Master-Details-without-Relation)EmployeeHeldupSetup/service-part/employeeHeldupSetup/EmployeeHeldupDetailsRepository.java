package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeHeldupDetailsRepository extends BaseRepository<EmployeeHeldupDetails> {
    List<EmployeeHeldupDetails> findByHeldupMasterIdAndIsDeletedFalse(Long heldupMasterId);
}
