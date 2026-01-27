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

    public List<BonusGenerationResponse> showBonusInformation(BonusGenerationRequest request) {

        if (request == null || request.getBonusYear() == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Bonus year is required");
        }

        // ================= STEP 1: Bonus Type Data =================
        YearlyBonusSetup bonusTypeData = bonusSetupRepository
                .findByBonusYearAndBonusPolicyAndBonusTypeAndIsDeletedFalse(
                        request.getBonusYear(),
                        request.getBonusPolicy(),
                        request.getBonusType()
                ).orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Yearly Bonus data not found"
                ));

        // ================= STEP 2: Bonus Policy =================
        BonusPolicy bonusPolicy = bonusPolicyRepository
                .findByIdAndIsDeletedFalse(bonusTypeData.getBonusPolicy())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "Bonus Policy not found"
                ));

        // ================= STEP 3: Appraisal Ratios =================
        List<ApprisalRatioSetup> apprisalRatios =
                apprisalRatioSetupRepository.findAllByIsDeletedFalse();

        if (apprisalRatios.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Apprisal ratio not configured");
        }

        List<EmployeeFestivalEligibility> eligibleEmployees;
        Set<String> employeeIds;

        // ================= Eligible Employee Fetch =================
        if (Boolean.TRUE.equals(request.getIsAllEmployees())) {

            eligibleEmployees =
                    eligibilityRepository.findAllByBonusIdAndIsDeletedFalse(bonusTypeData.getUuid());

            if (eligibleEmployees.isEmpty()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "Eligible employees not found");
            }

            employeeIds = eligibleEmployees.stream()
                    .map(EmployeeFestivalEligibility::getEmployeeId)
                    .collect(Collectors.toSet());

        } else {
            // Single employee
            EmployeeFestivalEligibility emp = eligibilityRepository
                    .findByEmployeeIdAndBonusIdAndIsDeletedFalse(
                            request.getEmployeeId(), bonusTypeData.getUuid()
                    ).orElseThrow(() -> new CustomException(
                            HttpStatus.NOT_FOUND,
                            "Employee eligibility not found"
                    ));

            eligibleEmployees = Collections.singletonList(emp);
            employeeIds = Collections.singleton(emp.getEmployeeId());
        }

        // ================= Salary Data =================
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

        // ================= Appraisal Ratio =================
        BigDecimal appraisalRatio = getExcellentRatio(bonusTypeData, apprisalRatios);

        // ================= Bonus Calculation =================
        List<BonusGenerationResponse> responses = new ArrayList<>();

        for (EmployeeSalaryResponse salary : salaryResponses) {

            BigDecimal finalBonus;

            if (Boolean.TRUE.equals(bonusPolicy.getIsProrataBasis())) {
                finalBonus = calculateProrataBonus(
                        salary,
                        bonusPolicy,
                        bonusTypeData,
                        apprisalRatios,
                        eligibleEmployees
                );
            } else {

                // Base bonus calculation
                BigDecimal baseBonus = calculateBaseBonus(salary, bonusPolicy);

                // Appraisal multiply
                finalBonus =
                        baseBonus.multiply(appraisalRatio);

                // Minimum bonus check
                finalBonus = applyMinimumBonus(finalBonus, bonusPolicy);
            }

            finalBonus = finalizeAmount(finalBonus);

            // Response build
            BonusGenerationResponse response = new BonusGenerationResponse();

            response.setEmployeeId(salary.getEmployeeId());
            response.setDesignation(salary.getDesignation());
            response.setDepartment(salary.getDepartment());
            response.setBonusAmount(finalBonus);
            response.setBonusYear(bonusTypeData.getBonusYear());
            response.setBonusId(bonusTypeData.getUuid());
            response.setBonusDate(bonusTypeData.getBonusDate());

            responses.add(response);
        }

        return responses;
    }

    //---------- get Basic amount ----------
    private BigDecimal getBasicAmount(EmployeeSalaryResponse salary) {
        return salary.getBreakups().stream()
                .filter(b ->
                        Boolean.TRUE.equals(
                                b.getSalaryStructureBreakup().getIsBasic()))
                .map(b -> BigDecimal.valueOf(b.getFinalAmount()))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    //---------- get Gross amount ----------
    private BigDecimal getGrossAmount(EmployeeSalaryResponse salary) {
        return BigDecimal.valueOf(salary.getGrossSalary());
    }

    private BigDecimal finalizeAmount(
            BigDecimal amount
    ) {
        return amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    //---------- Prorata type calculation ----------
    private BigDecimal calculateProrataBonus(
            EmployeeSalaryResponse salary,
            BonusPolicy policy,
            YearlyBonusSetup bonusTypeData,
            List<ApprisalRatioSetup> apprisalRatios,
            List<EmployeeFestivalEligibility> eligibleEmployees
    ) {

        // ================= STEP 1: Full Bonus =================
        BigDecimal fullBonus =
                calculateBaseBonus(salary, policy);

        // ================= STEP 2: Service Date =================
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

        LocalDate serviceStartDate;

        if ("joiningDate".equalsIgnoreCase(
                policy.getServiceLenBasedOn())) {

            serviceStartDate =
                    eligibility.getJoiningDate();

        } else {
            //serviceStartDate = null;
            serviceStartDate =
                    eligibility.getConfirmationDate();
        }

        if (serviceStartDate == null) {
            return BigDecimal.ZERO;
        }

        // ================= STEP 3: Service Days =================
        long serviceDays =
                ChronoUnit.DAYS.between(
                        serviceStartDate,
                        bonusTypeData.getBonusDate()
                );

        if (serviceDays <= 0) {
            return BigDecimal.ZERO;
        }

        // ================= STEP 4: Prorata Calculation =================
        BigDecimal prorataBonus;

        if (serviceDays >= 365) {

            prorataBonus = fullBonus;

        } else {

            BigDecimal oneDayBonus =
                    fullBonus.divide(
                            BigDecimal.valueOf(365),
                            6,
                            RoundingMode.HALF_UP
                    );

            prorataBonus =
                    oneDayBonus.multiply(
                            BigDecimal.valueOf(serviceDays)
                    );
        }

        // ================= STEP 5: Appraisal Ratio =================
        BigDecimal appraisalRatio =
                getExcellentRatio(
                        bonusTypeData,
                        apprisalRatios
                );

        BigDecimal finalBonus =
                prorataBonus.multiply(appraisalRatio);

        // ================= STEP 6: Minimum Bonus =================
        return applyMinimumBonus(finalBonus, policy);
    }

    //---------- When Prorata false ----------
    private BigDecimal calculateBaseBonus(
            EmployeeSalaryResponse salary,
            BonusPolicy policy
    ) {
        switch (policy.getConsiderType()) {

            case "basicPercent":
                return getBasicAmount(salary)
                        .multiply(
                                BigDecimal.valueOf(policy.getConsiderValue())
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                6,
                                RoundingMode.HALF_UP
                        );

            case "grossPercent":
                return getGrossAmount(salary)
                        .multiply(
                                BigDecimal.valueOf(policy.getConsiderValue())
                        )
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

    private BigDecimal getExcellentRatio(
            YearlyBonusSetup bonusTypeData,
            List<ApprisalRatioSetup> apprisalRatios
    ) {
        return apprisalRatios.stream()
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

}
