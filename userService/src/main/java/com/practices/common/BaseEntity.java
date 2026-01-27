package com.practices.common;

import jakarta.persistence.*;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void prePersist(){
        this.uuid=UUID.randomUUID().toString();
        this.isDeleted=false;
        this.createdDate=LocalDateTime.now();
        this.modifiedDate=LocalDateTime.now();

//    this.setUuid(UUID.randomUUID().toString());
//    this.setIsDeleted(false);
//    this.setCreatedDate(LocalDateTime.now());
//    this.setModifiedDate(LocalDateTime.now());

    }

    @PreUpdate
    protected void preUpdate(){
        this.modifiedDate=LocalDateTime.now();
//        this.setModifiedDate(LocalDateTime.now());
    }
}
