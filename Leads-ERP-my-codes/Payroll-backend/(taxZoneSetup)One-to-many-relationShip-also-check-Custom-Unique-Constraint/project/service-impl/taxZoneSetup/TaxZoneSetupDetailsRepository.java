package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaxZoneSetupDetailsRepository extends BaseRepository<TaxZoneSetupDetails> {
    Optional<TaxZoneSetupDetails> findByTaxCircleAndIsDeletedFalse(Long taxCircle);
}
