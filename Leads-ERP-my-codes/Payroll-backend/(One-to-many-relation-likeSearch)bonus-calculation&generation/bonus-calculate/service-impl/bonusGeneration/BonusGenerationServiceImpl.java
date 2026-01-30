package com.leads.microcube.process.bonusGeneration;

import com.leads.microcube.apprisalRatioSetup.ApprisalRatioSetup;
import com.leads.microcube.apprisalRatioSetup.ApprisalRatioSetupRepository;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.command.CommonRequest;
import com.leads.microcube.employeeSalary.query.EmployeeSalaryResponse;
import com.leads.microcube.payroll.bonusPolicy.BonusPolicy;
import com.leads.microcube.payroll.bonusPolicy.BonusPolicyRepository;
import com.leads.microcube.payroll.employeeSalary.EmployeeSalaryServiceImpl;
import com.leads.microcube.payroll.yearlyBonusSetup.YearlyBonusSetup;
import com.leads.microcube.payroll.yearlyBonusSetup.YearlyBonusSetupRepository;
import com.leads.microcube.process.bonusGeneration.command.BonusGenerationRequest;
import com.leads.microcube.process.bonusGeneration.query.BonusGenerationResponse;
import com.leads.microcube.tax.employeeFestivalEligibility.EmployeeFestivalEligibility;
import com.leads.microcube.tax.employeeFestivalEligibility.EmployeeFestivalEligibilityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BonusGenerationServiceImpl {

    private final YearlyBonusSetupRepository bonusSetupRepository;
    private final EmployeeFestivalEligibilityRepository eligibilityRepository;
    private final EmployeeSalaryServiceImpl employeeSalaryService;
    private final BonusPolicyRepository bonusPolicyRepository;
    private final ApprisalRatioSetupRepository apprisalRatioSetupRepository;


    public BonusGenerationServiceImpl(
            YearlyBonusSetupRepository bonusSetupRepository,
            EmployeeFestivalEligibilityRepository eligibilityRepository,
            EmployeeSalaryServiceImpl employeeSalaryService,
            BonusPolicyRepository bonusPolicyRepository,
            ApprisalRatioSetupRepository apprisalRatioSetupRepository
    ) {
        this.bonusSetupRepository = bonusSetupRepository;
        this.eligibilityRepository = eligibilityRepository;
        this.employeeSalaryService = employeeSalaryService;
        this.bonusPolicyRepository = bonusPolicyRepository;
        this.apprisalRatioSetupRepository = apprisalRatioSetupRepository;
    }

    public List<BonusGenerationResponse> showBonusInformation(
            BonusGenerationRequest request
    ) {

        if (request == null
                || request.getEmpOrgId() == null
                || request.getBonusYear() == null
                || request.getBonusType() == null) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Please fill up all required fields"
            );
        }

        // ================= STEP 1: Bonus Setup =================
        YearlyBonusSetup bonusTypeData =
                bonusSetupRepository
                        .findByBonusYearAndBonusTypeAndIsDeletedFalse(
                                request.getBonusYear(),
                                request.getBonusType()
                        )
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND,
                                "Yearly Bonus data not found"
                        ));

        // ================= STEP 2: Bonus Policy =================
        BonusPolicy bonusPolicy =
                bonusPolicyRepository
                        .findByIdAndIsDeletedFalse(
                                bonusTypeData.getBonusPolicy()
                        )
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND,
                                "Bonus Policy not found"
                        ));

        // ================= STEP 3: Appraisal Ratios (only if needed) =================
        List<ApprisalRatioSetup> apprisalRatios = Collections.emptyList();

        if (Boolean.TRUE.equals(bonusTypeData.getIsAppraisalRatio())) {
            apprisalRatios =
                    apprisalRatioSetupRepository.findAllByIsDeletedFalse();

            if (apprisalRatios.isEmpty()) {
                throw new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Apprisal ratio not configured"
                );
            }
        }

        // ================= STEP 4: Eligible Employees =================
        List<EmployeeFestivalEligibility> eligibleEmployees =
                eligibilityRepository.getEmployeeList(
                        request.getEmpOrgId(),
                        request.getEmpBranchId(),
                        request.getBonusYear(),
                        request.getBonusType()
                );

        if (eligibleEmployees.isEmpty()) {
            throw new CustomException(
                    HttpStatus.NOT_FOUND,
                    "Eligible employees not found"
            );
        }

        Set<String> employeeIds =
                eligibleEmployees.stream()
                        .map(EmployeeFestivalEligibility::getEmployeeId)
                        .collect(Collectors.toSet());

        Map<String, EmployeeFestivalEligibility> eligibilityMap =
                eligibleEmployees.stream()
                        .collect(Collectors.toMap(
                                EmployeeFestivalEligibility::getEmployeeId,
                                e -> e
                        ));

        // ================= STEP 5: Salary =================
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setReferenceCodes(employeeIds);

        List<EmployeeSalaryResponse> salaryResponses =
                employeeSalaryService.getStructureByEmpIds(commonRequest);

        if (salaryResponses.isEmpty()) {
            throw new CustomException(
                    HttpStatus.NOT_FOUND,
                    "Employee salary structure details not found"
            );
        }

        // ================= STEP 6: Bonus Calculation =================
        List<BonusGenerationResponse> responses = new ArrayList<>();

        for (EmployeeSalaryResponse salary : salaryResponses) {

            BigDecimal finalBonus;

            if (Boolean.TRUE.equals(bonusPolicy.getIsProrataBasis())) {

                finalBonus =
                        calculateProrataBonus(
                                salary,
                                bonusPolicy,
                                bonusTypeData,
                                apprisalRatios,
                                eligibleEmployees
                        );

            } else {

                BigDecimal baseBonus =
                        calculateBaseBonus(salary, bonusPolicy);

                BigDecimal afterAppraisal =
                        applyAppraisalRatioIfNeeded(
                                baseBonus,
                                bonusTypeData,
                                apprisalRatios
                        );

                finalBonus =
                        applyMinimumBonus(
                                afterAppraisal,
                                bonusPolicy
                        );
            }

            finalBonus = finalizeAmount(finalBonus);

            EmployeeFestivalEligibility eligibility =
                    eligibilityMap.get(salary.getEmployeeId());

            BonusGenerationResponse response =
                    new BonusGenerationResponse();

            response.setEmpOrgId(eligibility.getEmpOrgId());
            response.setEmpBranchId(eligibility.getEmpBranchId());
            response.setEmployeeName(eligibility.getEmployeeNameEn());
            response.setEmployeeId(salary.getEmployeeId());
            response.setDesignation(salary.getDesignation());
            response.setDepartment(salary.getDepartment());
            response.setBonusAmount(finalBonus);
            response.setBonusYear(bonusTypeData.getBonusYear());
            response.setBonusType(bonusTypeData.getBonusType());
            response.setBonusDate(bonusTypeData.getBonusDate());
            response.setYearlyBonusUuid(bonusTypeData.getUuid());

            responses.add(response);
        }

        return responses;
    }

    // ================= Utility Methods =================

    private BigDecimal calculateProrataBonus(
            EmployeeSalaryResponse salary,
            BonusPolicy policy,
            YearlyBonusSetup bonusTypeData,
            List<ApprisalRatioSetup> apprisalRatios,
            List<EmployeeFestivalEligibility> eligibleEmployees
    ) {

        BigDecimal fullBonus =
                calculateBaseBonus(salary, policy);

        EmployeeFestivalEligibility eligibility =
                eligibleEmployees.stream()
                        .filter(e ->
                                e.getEmployeeId()
                                        .equals(salary.getEmployeeId()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND,
                                "Employee eligibility not found"
                        ));

        LocalDate serviceStartDate =
                "joiningDate".equalsIgnoreCase(
                        policy.getServiceLenBasedOn()
                )
                        ? eligibility.getJoiningDate()
                        : eligibility.getConfirmationDate();

        if (serviceStartDate == null) {
            return BigDecimal.ZERO;
        }

        long serviceDays =
                ChronoUnit.DAYS.between(
                        serviceStartDate,
                        bonusTypeData.getBonusDate()
                );

        if (serviceDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal prorataBonus =
                serviceDays >= 365
                        ? fullBonus
                        : fullBonus
                        .divide(
                                BigDecimal.valueOf(365),
                                6,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                BigDecimal.valueOf(serviceDays)
                        );

        BigDecimal afterAppraisal =
                applyAppraisalRatioIfNeeded(
                        prorataBonus,
                        bonusTypeData,
                        apprisalRatios
                );

        return applyMinimumBonus(afterAppraisal, policy);
    }

    private BigDecimal applyAppraisalRatioIfNeeded(
            BigDecimal bonus,
            YearlyBonusSetup bonusTypeData,
            List<ApprisalRatioSetup> apprisalRatios
    ) {
        if (!Boolean.TRUE.equals(bonusTypeData.getIsAppraisalRatio())) {
            return bonus;
        }

        BigDecimal ratio =
                apprisalRatios.stream()
                        .filter(a ->
                                a.getPhaseId()
                                        .equals(
                                                bonusTypeData.getAppraisalPhase()
                                        ))
                        .findFirst()
                        .map(ApprisalRatioSetup::getExcellent)
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND,
                                "Appraisal phase not found"
                        ));

        return bonus.multiply(ratio);
    }

    private BigDecimal calculateBaseBonus(
            EmployeeSalaryResponse salary,
            BonusPolicy policy
    ) {
        switch (policy.getConsiderType()) {

            case "basicPercent":
                return getBasicAmount(salary)
                        .multiply(
                                BigDecimal.valueOf(
                                        policy.getConsiderValue()
                                ))
                        .divide(
                                BigDecimal.valueOf(100),
                                6,
                                RoundingMode.HALF_UP
                        );

            case "grossPercent":
                return getGrossAmount(salary)
                        .multiply(
                                BigDecimal.valueOf(
                                        policy.getConsiderValue()
                                ))
                        .divide(
                                BigDecimal.valueOf(100),
                                6,
                                RoundingMode.HALF_UP
                        );

            case "fixedAmount":
                return BigDecimal.valueOf(
                        policy.getConsiderValue()
                );

            default:
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid consider type"
                );
        }
    }

    private BigDecimal applyMinimumBonus(
            BigDecimal bonus,
            BonusPolicy policy
    ) {
        BigDecimal minimum =
                BigDecimal.valueOf(
                        policy.getMinimumBonusAmount()
                );

        if (minimum.compareTo(BigDecimal.ZERO) <= 0) {
            return bonus;
        }

        return bonus.compareTo(minimum) < 0
                ? minimum
                : bonus;
    }

    private BigDecimal finalizeAmount(
            BigDecimal amount
    ) {
        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal getBasicAmount(
            EmployeeSalaryResponse salary
    ) {
        return salary.getBreakups().stream()
                .filter(b ->
                        Boolean.TRUE.equals(
                                b.getSalaryStructureBreakup()
                                        .getIsBasic()))
                .map(b ->
                        BigDecimal.valueOf(
                                b.getFinalAmount()))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getGrossAmount(
            EmployeeSalaryResponse salary
    ) {
        return BigDecimal.valueOf(
                salary.getGrossSalary()
        );
    }

}
