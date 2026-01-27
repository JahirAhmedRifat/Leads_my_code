package com.leads.microcube.infrastructure.client;

import com.leads.microcube.base.command.CommonRequest;
import com.leads.microcube.helper.TokenClaimUtils;
import com.leads.microcube.process.EmployeeResponse;
import com.leads.microcube.process.MonthlyAttendanceSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class LeaveAttendanceClient {
    private final WebClient client;
    private final ServiceDiscoveryClient discoveryClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveAttendanceClient.class);
    public LeaveAttendanceClient(WebClient webClient, ServiceDiscoveryClient discoveryClient) {
        this.client = webClient;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Get employee
     * Endpoint: GET /employee-information/get-employees-for-salary-process
     */
    public Mono<Map<String, Double>> getMonthlyAttendanceSummary(CommonRequest request) {
        String url = discoveryClient.getLeaveAttendanceServiceUrl();
        return client.post()
                .uri(url + "attendance-flag/monthly-attendance-summary")
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Double>>() {})
                .doOnError(error -> LOGGER.error("Error for fetching monthly attendanceSummary: {}", error.getMessage()));
    }
}
