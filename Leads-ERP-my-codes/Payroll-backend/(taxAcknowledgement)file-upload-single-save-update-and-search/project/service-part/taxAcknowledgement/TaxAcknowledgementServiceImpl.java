package com.leads.microcube.tax.taxAcknowledgement;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.infrastructure.FileUploadService;
import com.leads.microcube.tax.taxAcknowledgement.command.TaxAcknowledgementRequest;
import com.leads.microcube.tax.taxAcknowledgement.query.TaxAcknowledgementResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TaxAcknowledgementServiceImpl
        extends BaseServiceImpl<TaxAcknowledgement, TaxAcknowledgementRequest, TaxAcknowledgementResponse>
        implements TaxAcknowledgementService, TaxAcknowledgementQueryService {

    private final TaxAcknowledgementRepository taxacknowledgementRepository;
    private final FileUploadService fileUploadService;
    private static final String BUCKET_NAME = "taxAcknowledgement";

    public TaxAcknowledgementServiceImpl(
            BaseRepository<TaxAcknowledgement> repository,
            TaxAcknowledgementRepository taxacknowledgementRepository,
            FileUploadService fileUploadService
    ) {
        super(repository);
        this.taxacknowledgementRepository = taxacknowledgementRepository;
        this.fileUploadService = fileUploadService;
    }


    @Override
    @Transactional
    public TaxAcknowledgementResponse saveData(TaxAcknowledgementRequest request) {

        if (request == null || request.getEmployeeId() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "No Data provided"
            );
        }

        TaxAcknowledgement entity = new TaxAcknowledgement();
        BeanUtils.copyProperties(
                request,
                entity,
                "id", "uuid", "isDeleted",
                "tinAttachmentPath", "acknowledgementPath"
        );

        if (request.getTinAttachmentPath() != null && !request.getTinAttachmentPath().isEmpty()) {
            String fileUrl = fileUploadService
                    .uploadFile(request.getTinAttachmentPath(), BUCKET_NAME)
                    .orElse(null);
            entity.setTinAttachmentPath(fileUrl);
        }

        if (request.getAcknowledgementPath() != null && !request.getAcknowledgementPath().isEmpty()) {
            String fileUrl = fileUploadService
                    .uploadFile(request.getAcknowledgementPath(), BUCKET_NAME)
                    .orElse(null);
            entity.setAcknowledgementPath(fileUrl);
        }

        TaxAcknowledgement savedEntity = taxacknowledgementRepository.save(entity);

        return toResponse(savedEntity);
    }

    @Override
    @Transactional
    public TaxAcknowledgementResponse updateData(TaxAcknowledgementRequest request) {

        if (request == null || request.getUuid() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "UUID is required for update"
            );
        }

        TaxAcknowledgement entity = taxacknowledgementRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Tax acknowledgement not found"
                ));

        // Update allowed simple fields
        entity.setDateOfSubmission(request.getDateOfSubmission());
        entity.setSerialNo(request.getSerialNo());
        entity.setRemarks(request.getRemarks());


        // Update TIN attachment if provided
        if (request.getTinAttachmentPath() != null && !request.getTinAttachmentPath().isEmpty()) {
            String fileUrl = fileUploadService
                    .uploadFile(request.getTinAttachmentPath(), BUCKET_NAME)
                    .orElse(null);
            entity.setTinAttachmentPath(fileUrl);
        }

        // Update acknowledgement attachment if provided
        if (request.getAcknowledgementPath() != null && !request.getAcknowledgementPath().isEmpty()) {
            String fileUrl = fileUploadService
                    .uploadFile(request.getAcknowledgementPath(), BUCKET_NAME)
                    .orElse(null);
            entity.setAcknowledgementPath(fileUrl);
        }

        TaxAcknowledgement updatedEntity = taxacknowledgementRepository.save(entity);

        return toResponse(updatedEntity);
    }

    @Override
    public PageResponse<TaxAcknowledgementResponse> searchCriteria(
            String employeeId,
            Integer taxYear,
            int pageIndex,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<TaxAcknowledgement> res = taxacknowledgementRepository.searchCriteria(
                employeeId,
                taxYear,
                pageable
        );

        Page<TaxAcknowledgementResponse> responsePage = res.map(
                TaxAcknowledgement ->
                        this.convertToTarget(TaxAcknowledgement, TaxAcknowledgementResponse.class)
        );
        return toPageResponse(responsePage);
    }
}
