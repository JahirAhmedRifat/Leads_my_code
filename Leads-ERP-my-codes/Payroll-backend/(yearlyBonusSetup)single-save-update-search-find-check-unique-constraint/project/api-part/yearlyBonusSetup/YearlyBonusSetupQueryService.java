package com.leads.microcube.payroll.yearlyBonusSetup;

import com.leads.microcube.base.query.PageResponse;
import com.leads.microcube.payroll.yearlyBonusSetup.query.YearlyBonusSetupResponse;

public interface YearlyBonusSetupQueryService {

    PageResponse<YearlyBonusSetupResponse> searchCriteria(
            Integer bonusYear,
            Long bonusPolicy,
            String bonusType,
            int pageIndex,
            int pageSize
    );

    YearlyBonusSetupResponse findByBonusYearAndPolicyAndType(
            Integer bonusYear,
            Long bonusPolicy,
            String bonusType
    );

}