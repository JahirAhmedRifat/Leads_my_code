package com.leads.microcube.base;



import com.leads.microcube.helper.TokenClaimUtils;
import com.leads.model.ActionType;
import com.leads.model.ApprovalBaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Setter
@Getter
@Accessors(chain = true)
public abstract class BaseEntity extends ApprovalBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String uuid;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    @Column(name = "org_id")
    private String orgId;
    @NotNull
    @Column(name = "office_id")
    private String officeId;

    @Override
    public void onPrePersist() {
        this.setUuid(IdGeneratorHelper.generateUUid());
        this.setIsDeleted(false);
        this.setActionType(ActionType.CREATE);
        this.setRecordUserId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getUsername());
        this.setRecordDt(LocalDateTime.now());
        this.setOrgId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getOrgId());
        this.setOfficeId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getOfficeId());
        super.onPrePersist();
    }

    @Override
    public void onPreUpdate() {
        this.setRecordUserId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getUsername());
        this.setRecordDt(LocalDateTime.now());
        this.setOrgId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getOrgId());
        this.setOfficeId(Objects.requireNonNull(TokenClaimUtils.extractAllClaims()).getOfficeId());
        super.onPreUpdate();
    }

}
