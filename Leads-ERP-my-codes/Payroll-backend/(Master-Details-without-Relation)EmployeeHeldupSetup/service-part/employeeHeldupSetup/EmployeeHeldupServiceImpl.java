package com.leads.microcube.payroll.employeeHeldupSetup;

import com.leads.microcube.base.BaseRepository;
import com.leads.microcube.base.BaseServiceImpl;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.command.EmployeeHeldupSearchRequest;
import com.leads.microcube.payroll.employeeHeldupSetup.query.EmployeeHeldupResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class EmployeeHeldupServiceImpl
        extends BaseServiceImpl<EmployeeHeldup, EmployeeHeldupRequest, EmployeeHeldupResponse>
        implements EmployeeHeldupService, EmployeeHeldupQueryService {

    private final EmployeeHeldupRepository heldupRepository;
    private final EmployeeHeldupDetailsRepository heldupDetailsRepository;

    public EmployeeHeldupServiceImpl(
            BaseRepository<EmployeeHeldup> repository,
            EmployeeHeldupRepository heldupRepository,
            EmployeeHeldupDetailsRepository heldupDetailsRepository
    ) {
        super(repository);
        this.heldupRepository = heldupRepository;
        this.heldupDetailsRepository = heldupDetailsRepository;
    }

    @Override
    @Transactional
    public EmployeeHeldupResponse heldUpCreate(EmployeeHeldupRequest request) {

        //  Check validation
        if (request == null
                || request.getEmployeeId() == null
                || request.getHeldupType() == null
                || request.getFromDate() == null
                || request.getToDate() == null) {

            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Please fill in all required fields."
            );
        }

        // Normalize dates (MONTH-YEAR handling)
        LocalDate fromDate = request.getFromDate().withDayOfMonth(1);
        LocalDate toDate = request.getToDate()
                .withDayOfMonth(request.getToDate().lengthOfMonth());

        if (fromDate.isAfter(toDate)) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "From date cannot be greater than to date"
            );
        }

        //  Overlap check(between date range)
        boolean exists = heldupRepository.existsOverlappingHeldup(
                request.getEmployeeId(),
                request.getHeldupType(),
                fromDate,
                toDate
        );

        if (exists) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Heldup already exists for this employee within the selected date range"
            );
        }

        //  Save MASTER
        EmployeeHeldup master = new EmployeeHeldup();
        BeanUtils.copyProperties(
                request,
                master,
                "id", "uuid", "isDeleted", "heldupDetails"
        );

        master.setFromDate(fromDate);
        master.setToDate(toDate);

        EmployeeHeldup masterSaved = heldupRepository.save(master);

        //  Save DETAILS (OPTIONAL)
        List<EmployeeHeldupResponse.EmployeeHeldupDetailsResponse> detailResponses = new ArrayList<>();

        if (request.getHeldupDetails() != null && !request.getHeldupDetails().isEmpty()) {

            for (EmployeeHeldupRequest.EmployeeHeldupDetailsRequest d
                    : request.getHeldupDetails()) {

                if (d.getSalaryStructureBreakupId() == null
                        || d.getHeldupValue() == null) {

                    throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "Salary structure breakup and heldup value are required for heldup details"
                    );
                }

                EmployeeHeldupDetails detail = new EmployeeHeldupDetails();
                BeanUtils.copyProperties(d, detail, "id", "uuid", "isDeleted");

                detail.setHeldupMasterId(masterSaved.getId());

                EmployeeHeldupDetails savedDetail =
                        heldupDetailsRepository.save(detail);

                EmployeeHeldupResponse.EmployeeHeldupDetailsResponse dr =
                        new EmployeeHeldupResponse.EmployeeHeldupDetailsResponse();

                BeanUtils.copyProperties(savedDetail, dr);

                detailResponses.add(dr);
            }
        }

        // Build FINAL RESPONSE
        EmployeeHeldupResponse response = new EmployeeHeldupResponse();
        BeanUtils.copyProperties(masterSaved, response);
        response.setHeldupDetails(detailResponses);

        return response;
    }


    @Override
    @Transactional
    public EmployeeHeldupResponse updateHeldup(EmployeeHeldupRequest request) {

        //  Validation
        if (request == null
                || request.getUuid() == null
                || request.getEmployeeId() == null
                || request.getHeldupType() == null
                || request.getFromDate() == null
                || request.getToDate() == null) {

            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Heldup id, employee, heldup type, from date and to date are required"
            );
        }

        //  Normalize dates (MONTH-YEAR)
        LocalDate fromDate = request.getFromDate().withDayOfMonth(1);
        LocalDate toDate = request.getToDate()
                .withDayOfMonth(request.getToDate().lengthOfMonth());

        if (fromDate.isAfter(toDate)) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "From date cannot be greater than to date"
            );
        }

        //  Overlap check (EXCLUDE SELF)
        boolean exists = heldupRepository.existsOverlappingHeldupExcludeSelf(
                request.getEmployeeId(),
                request.getHeldupType(),
                request.getUuid(),
                fromDate,
                toDate
        );

        if (exists) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Heldup already exists for this employee within the selected date range"
            );
        }

        //  Fetch MASTER
        EmployeeHeldup master = heldupRepository
                .findByUuidAndIsDeletedFalse(request.getUuid())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Heldup master data not found"
                ));

        //  Update allowed fields
        master.setFromDate(fromDate);
        master.setToDate(toDate);
        master.setRemarks(request.getRemarks());

        EmployeeHeldup masterUpdated = heldupRepository.save(master);

        //  Update DETAILS (OPTIONAL)
        List<EmployeeHeldupResponse.EmployeeHeldupDetailsResponse> detailResponses =
                new ArrayList<>();

        if (request.getHeldupDetails() != null && !request.getHeldupDetails().isEmpty()) {

            for (EmployeeHeldupRequest.EmployeeHeldupDetailsRequest d
                    : request.getHeldupDetails()) {

                if (d.getUuid() == null) {
                    throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "Heldup detail uuid is required for update"
                    );
                }

                if (d.getHeldupValue() == null) {
                    throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "Heldup value is required for update"
                    );
                }

                EmployeeHeldupDetails detail = heldupDetailsRepository
                        .findByUuidAndIsDeletedFalse(d.getUuid())
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND,
                                "Heldup detail not found"
                        ));

                if (!detail.getHeldupMasterId().equals(masterUpdated.getId())) {
                    throw new CustomException(
                            HttpStatus.BAD_REQUEST,
                            "Heldup detail does not belong to this master"
                    );
                }

                detail.setHeldupValue(d.getHeldupValue());

                EmployeeHeldupDetails savedDetail =
                        heldupDetailsRepository.save(detail);

                EmployeeHeldupResponse.EmployeeHeldupDetailsResponse dr =
                        new EmployeeHeldupResponse.EmployeeHeldupDetailsResponse();

                BeanUtils.copyProperties(savedDetail, dr);
                detailResponses.add(dr);
            }
        }

        //  Build RESPONSE
        EmployeeHeldupResponse response = new EmployeeHeldupResponse();
        BeanUtils.copyProperties(masterUpdated, response);
        response.setHeldupDetails(detailResponses);

        return response;
    }


    @Override
    public EmployeeHeldupResponse getInfoById(String uuid) {

        // Check Validation
        if (uuid == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Heldup id is required"
            );
        }

        // Fetch MASTER
        EmployeeHeldup master = heldupRepository
                .findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Data not found"
                ));

        //  Fetch DETAILS
        List<EmployeeHeldupDetails> details =
                heldupDetailsRepository
                        .findByHeldupMasterIdAndIsDeletedFalse(master.getId());


        // Convert DETAILS → RESPONSE
        List<EmployeeHeldupResponse.EmployeeHeldupDetailsResponse> detailResponses = new ArrayList<>();

        for (EmployeeHeldupDetails d : details) {

            EmployeeHeldupResponse.EmployeeHeldupDetailsResponse dr =
                    new EmployeeHeldupResponse.EmployeeHeldupDetailsResponse();

            BeanUtils.copyProperties(d, dr);
            detailResponses.add(dr);
        }

        // Map MASTER → RESPONSE
        EmployeeHeldupResponse response = new EmployeeHeldupResponse();
        BeanUtils.copyProperties(master, response);
        response.setHeldupDetails(detailResponses);

        return response;
    }

    @Override
    public PageResponse<EmployeeHeldupResponse> searchCriteria(
            EmployeeHeldupSearchRequest request
    ) {

        if (request == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Search request cannot be null"
            );
        }

        // Pagination
        Pageable pageable =
                PageRequest.of(
                        request.getPageIndex(),
                        request.getPageSize(),
                        Sort.by("id").descending()
                );

        // Normalize dates (MONTH-YEAR search)
        LocalDate fromDate = null;
        LocalDate toDate = null;

        if (request.getFromDate() != null) {
            fromDate = request.getFromDate().withDayOfMonth(1);
        }

        if (request.getToDate() != null) {
            toDate = request.getToDate()
                    .withDayOfMonth(request.getToDate().lengthOfMonth());
        }

        // Search
        Page<EmployeeHeldup> result =
                heldupRepository.searchCriteria(
                        request.getEmployeeId(),
                        request.getHeldupType(),
                        fromDate,
                        toDate,
                        pageable
                );

        // Convert → Response
        Page<EmployeeHeldupResponse> responsePage =
                result.map(
                        entity ->
                                this.convertToTarget(entity, EmployeeHeldupResponse.class)
                );

        return toPageResponse(responsePage);
    }

}
