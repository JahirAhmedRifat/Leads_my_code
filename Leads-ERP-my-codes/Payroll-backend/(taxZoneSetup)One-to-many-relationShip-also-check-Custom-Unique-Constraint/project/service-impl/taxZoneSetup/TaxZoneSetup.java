package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tax_zone_setup")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaxZoneSetup extends BaseEntity {

    @Column(name = "tax_zone", nullable = false)
    private Long taxZone;

    @OneToMany(mappedBy = "taxZoneSetup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxZoneSetupDetails> taxZoneDetails = new ArrayList<>();

}
