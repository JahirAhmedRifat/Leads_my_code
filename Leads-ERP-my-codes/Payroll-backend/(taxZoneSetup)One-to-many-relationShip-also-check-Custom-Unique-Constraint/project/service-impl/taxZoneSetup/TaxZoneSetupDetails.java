package com.leads.microcube.tax.taxZoneSetup;


import com.leads.microcube.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tax_zone_setup_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaxZoneSetupDetails extends BaseEntity {

    @Column(name = "tax_circle", nullable = false)
    private Long taxCircle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_zone_id", nullable = false)
    private TaxZoneSetup taxZoneSetup;

}
