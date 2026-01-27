package com.leads.microcube.tax.taxZoneSetup.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leads.microcube.base.command.IdHolder;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxZoneSetupRequest extends IdHolder {

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @NotNull(message = "Tax Zone is required")
    private Long taxZone;

    @NotNull(message = "Tax Zone details are required")
    private List<TaxZoneSetupDetailsRequest> taxZoneDetails;

    @Data
    public static class TaxZoneSetupDetailsRequest extends IdHolder {

        @JsonProperty("isDeleted")
        private Boolean isDeleted;

        @NotNull(message = "Tax Circle is required")
        private Long taxCircle;
    }
}
