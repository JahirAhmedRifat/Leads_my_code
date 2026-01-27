package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaxZoneSetupRepository extends BaseRepository<TaxZoneSetup> {
    Optional<TaxZoneSetup> findByTaxZoneAndIsDeletedFalse(Long taxZone);
    Optional<TaxZoneSetup> findByUuidAndIsDeletedFalse(String uuid);
}
