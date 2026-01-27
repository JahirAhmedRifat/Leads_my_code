package com.leads.microcube.tax.taxZoneSetup;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.tax.taxZoneSetup.command.TaxZoneSetupRequest;
import com.leads.microcube.tax.taxZoneSetup.query.TaxZoneSetupResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaxZoneSetupServiceImpl
        extends BaseServiceImpl<TaxZoneSetup, TaxZoneSetupRequest, TaxZoneSetupResponse>
        implements TaxZoneSetupService, TaxZoneSetupQueryService {

    private final TaxZoneSetupRepository taxZoneSetupRepository;
    private final TaxZoneSetupDetailsRepository taxZoneSetupDetailsRepository;

    public TaxZoneSetupServiceImpl(
            BaseRepository<TaxZoneSetup> repository,
            TaxZoneSetupRepository taxZoneSetupRepository,
            TaxZoneSetupDetailsRepository taxZoneSetupDetailsRepository
    ) {
        super(repository);
        this.taxZoneSetupRepository = taxZoneSetupRepository;
        this.taxZoneSetupDetailsRepository = taxZoneSetupDetailsRepository;
    }

    @Override
    @Transactional
    public TaxZoneSetupResponse createTaxZone(TaxZoneSetupRequest request) {
        try {

            // Collect all conflicts
            StringBuilder conflictMessage = new StringBuilder();

            for (TaxZoneSetupRequest.TaxZoneSetupDetailsRequest detailReq
                    : request.getTaxZoneDetails()) {

                taxZoneSetupDetailsRepository
                        .findByTaxCircleAndIsDeletedFalse(detailReq.getTaxCircle())
                        .ifPresent(existing -> {
                            Long existingTaxZone = existing.getTaxZoneSetup().getTaxZone();

                            conflictMessage.append("Tax Circle [")
                                    .append(detailReq.getTaxCircle())
                                    .append("] already exists under Tax Zone [")
                                    .append(existingTaxZone)
                                    .append("]\n");
                        });
            }

            // If any conflicts found → throw once
            if (!conflictMessage.isEmpty()) {
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        conflictMessage.toString().trim()
                );
            }

            // Create parent entity
            TaxZoneSetup entity = new TaxZoneSetup();
            BeanUtils.copyProperties(request, entity, "taxZoneDetails");

            // Map child entities
            entity.setTaxZoneDetails(
                    request.getTaxZoneDetails().stream()
                            .map(d -> {
                                TaxZoneSetupDetails detail = new TaxZoneSetupDetails();
                                BeanUtils.copyProperties(d, detail);
                                detail.setTaxZoneSetup(entity);
                                return detail;
                            })
                            .collect(Collectors.toList())
            );

            // Save
            TaxZoneSetup saved = taxZoneSetupRepository.save(entity);
            return toResponse(saved);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional
    public TaxZoneSetupResponse updateTaxZone(TaxZoneSetupRequest request) {
        try {
            // Find existing entity
            TaxZoneSetup entity = taxZoneSetupRepository
                    .findByUuidAndIsDeletedFalse(request.getUuid())
                    .orElseThrow(() -> new CustomException(
                            HttpStatus.NOT_FOUND,
                            "TaxZone not found"
                    ));

            // Update parent fields
            BeanUtils.copyProperties(request, entity, "taxZoneDetails", "id", "uuid");

            // Create map of existing details by UUID (only non-deleted ones)
            Map<String, TaxZoneSetupDetails> existingMap = entity.getTaxZoneDetails().stream()
                    .filter(d -> Boolean.FALSE.equals(d.getIsDeleted()))
                    .collect(Collectors.toMap(
                            TaxZoneSetupDetails::getUuid,
                            d -> d
                    ));

            StringBuilder conflictMessage = new StringBuilder();

            Set<String> processedUuids = new HashSet<>();

            // Process incoming child requests
            for (TaxZoneSetupRequest.TaxZoneSetupDetailsRequest d : request.getTaxZoneDetails()) {
                if (d.getUuid() != null && !d.getUuid().isBlank()) {
                    processedUuids.add(d.getUuid());

                    TaxZoneSetupDetails existingDetail = existingMap.get(d.getUuid());
                    if (existingDetail == null) {
                        continue;
                    }

                    if (Boolean.TRUE.equals(d.getIsDeleted())) {
                        existingDetail.setIsDeleted(true);
                        continue;
                    }

                    // Check uniqueness for tax circle (whether changed or not)
                    taxZoneSetupDetailsRepository
                            .findByTaxCircleAndIsDeletedFalse(d.getTaxCircle())
                            .ifPresent(found -> {
                                if (!found.getUuid().equals(existingDetail.getUuid())) {
                                    conflictMessage.append("Tax Circle [")
                                            .append(d.getTaxCircle())
                                            .append("] already exists under Tax Zone [")
                                            .append(found.getTaxZoneSetup().getTaxZone())
                                            .append("]\n");
                                }
                            });

                    // Update existing detail
                    existingDetail.setTaxCircle(d.getTaxCircle());
                    existingDetail.setIsDeleted(false);
                    BeanUtils.copyProperties(d, existingDetail, "id", "uuid", "taxZoneSetup", "isDeleted");

                } else {
                    // new Save  - no UUID
                    // Uniqueness check for new tax circle
                    taxZoneSetupDetailsRepository
                            .findByTaxCircleAndIsDeletedFalse(d.getTaxCircle())
                            .ifPresent(found -> conflictMessage.append("Tax Circle [")
                                    .append(d.getTaxCircle())
                                    .append("] already exists under Tax Zone [")
                                    .append(found.getTaxZoneSetup().getTaxZone())
                                    .append("]\n"));

                    // Create new detail
                    TaxZoneSetupDetails newDetail = new TaxZoneSetupDetails();
                    BeanUtils.copyProperties(d, newDetail, "id", "uuid", "taxZoneSetup");
                    newDetail.setTaxZoneSetup(entity);
                    newDetail.setIsDeleted(false);
                    entity.getTaxZoneDetails().add(newDetail);
                }
            }

            // Handle items that exist in DB but not in the request (soft delete them)
            for (Map.Entry<String, TaxZoneSetupDetails> entry : existingMap.entrySet()) {
                if (!processedUuids.contains(entry.getKey())) {
                    // This detail exists in DB but wasn't sent in request, so mark as deleted
                    entry.getValue().setIsDeleted(true);
                }
            }

            // Throw if any conflicts
            if (!conflictMessage.isEmpty()) {
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        conflictMessage.toString().trim()
                );
            }

            // Save the entity
            TaxZoneSetup saved = taxZoneSetupRepository.save(entity);

            // For response, get only non-deleted items
            List<TaxZoneSetupDetails> nonDeletedDetails = saved.getTaxZoneDetails().stream()
                    .filter(d -> Boolean.FALSE.equals(d.getIsDeleted()))
                    .collect(Collectors.toList());

            // Create a new TaxZoneSetup object for response to avoid modifying entity state
            TaxZoneSetup responseEntity = new TaxZoneSetup();
            BeanUtils.copyProperties(saved, responseEntity, "taxZoneDetails");
            responseEntity.setTaxZoneDetails(nonDeletedDetails);

            return toResponse(responseEntity);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaxZoneSetupResponse getTaxZoneByTaxId(Long taxZoneId) {

        TaxZoneSetup entity = taxZoneSetupRepository
                .findByTaxZoneAndIsDeletedFalse(taxZoneId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Tax Zone [" + taxZoneId + "] not found"
                ));

        // Filter only non-deleted child records
        entity.setTaxZoneDetails(
                entity.getTaxZoneDetails().stream()
                        .filter(d -> Boolean.FALSE.equals(d.getIsDeleted()))
                        .collect(Collectors.toList())
        );

        return toResponse(entity);
    }

}
