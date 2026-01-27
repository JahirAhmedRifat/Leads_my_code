package com.leads.microcube.tax.taxZoneSetup.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import lombok.Data;
import java.util.List;

@Data
public class TaxZoneSetupResponse extends IdHolder {
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    private Long taxZone;
    private List<TaxZoneSetupDetailsResponse> taxZoneDetails;

    @Data
    public static class TaxZoneSetupDetailsResponse extends IdHolder {
        @JsonProperty("isDeleted")
        private Boolean isDeleted;
        private Long taxCircle;
    }
}
