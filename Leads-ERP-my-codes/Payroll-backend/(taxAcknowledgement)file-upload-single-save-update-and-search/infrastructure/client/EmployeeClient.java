package com.leads.microcube.infrastructure.client;

import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.helper.TokenClaimUtils;
import com.leads.microcube.process.EmployeeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

/**
 * Client for communicating with Employee Management Service
 * Used to validate employee existence, status, and fetch employee details
 */
@Component
public class EmployeeClient {
  private final WebClient client;
  private final ServiceDiscoveryClient discoveryClient;
  private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeClient.class);

  public EmployeeClient(WebClient webClient, ServiceDiscoveryClient discoveryClient) {
    this.client = webClient;
    this.discoveryClient = discoveryClient;
  }

  /**
   * Get employee basic info by employee ID
   * This is used to validate employee existence and get active status
   * Endpoint: GET /employee-information/basicInfo/{employeeId}
   *
   * @param employeeId The employee ID to fetch
   * @return Mono<Envelope> containing employee basic information
   */
  public Mono<Envelope> getBasicInfoByEmployeeId(String employeeId) {
    String url = discoveryClient.getEmployeeServiceUrl();
    LOGGER.debug("Fetching employee basic info for employeeId: {}", employeeId);
    return client.get()
        .uri(url + "employee-information/basicInfo/" + employeeId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError() || status.is5xxServerError(),
            response -> {
              LOGGER.error("Employee Service returned error status: {} for employee: {}",
                  response.statusCode(), employeeId);
              return Mono.empty();
            })
        .bodyToMono(Envelope.class)
        .onErrorResume(error -> {
          LOGGER.error("Error fetching employee info for {}: {}", employeeId, error.getMessage());
          return Mono.empty();
        })
        .doOnSuccess(envelope -> {
          if (envelope != null) {
            LOGGER.debug("Successfully fetched employee info for: {}", employeeId);
          }
        });
  }
//    public Mono<Envelope> getBasicInfoByListEmployeeId(List<String> employeeIds) {
//        String url = discoveryClient.getLocalEmployeeServiceUrl() + "employee-information/basicInfoList";
//
//        // build query params correctly
//        String params = employeeIds.stream()
//                .map(id -> "employeeId=" + id)
//                .collect(Collectors.joining("&"));
//
//        String finalUrl = url + "?" + params;
//
//        return client.get()
//                .uri(finalUrl)
//                .header("Authorization", "Bearer " + TokenClaimUtils.getToken())
//                .retrieve()
//                .bodyToMono(Envelope.class)
//                .doOnNext(env -> LOGGER.info("Employee info envelope: {}", env))
//                .onErrorResume(error -> {
//                    LOGGER.error("Error fetching employee info for {}: {}", employeeIds, error.getMessage());
//                    return Mono.just(new Envelope()
//                            .setStatus(false)
//                            .setMessage("Employee service error: " + error.getMessage())
//                            .setPayload(null));
//                });
//    }


    public Mono<Envelope> getBasicInfoByListEmployeeId(List<String> employeeIds) {
        String url = discoveryClient.getLocalEmployeeServiceUrl() + "employee-information/basicInfoList";

        EmployeeIdsRequest requestBody = new EmployeeIdsRequest();
        requestBody.setEmployeeIds(employeeIds); // your List<String>

        return client.post()
                .uri(url)
                .header("Authorization", "Bearer " + TokenClaimUtils.getToken())
                .bodyValue(requestBody) // send object, not raw list
                .retrieve()
                .bodyToMono(Envelope.class);
    }





    /**
   * Search employees by various criteria
   * Endpoint: GET /employee-information/search-employees
   *
   * @param employeeId           Filter by employee ID
   * @param name                 Filter by name
   * @param jobStatus            Filter by job status (e.g., "Active", "Inactive")
   * @param department           Filter by department
   * @param corporateDesignation Filter by designation
   * @param employmentType       Filter by employment type
   * @param unmapped             Filter unmapped employees
   * @return Mono<Envelope> containing list of employees matching criteria
   */
  public Mono<Envelope> searchEmployees(String employeeId, String name, String jobStatus,
      String department, String corporateDesignation,
      String employmentType, Boolean unmapped) {
    String url = discoveryClient.getEmployeeServiceUrl();
    LOGGER.debug("Searching employees with criteria - employeeId: {}, name: {}, jobStatus: {}",
        employeeId, name, jobStatus);

    StringBuilder uriBuilder = new StringBuilder(url + "employee-information/search-employees?");
    if (employeeId != null)
      uriBuilder.append("employeeId=").append(employeeId).append("&");
    if (name != null)
      uriBuilder.append("name=").append(name).append("&");
    if (jobStatus != null)
      uriBuilder.append("jobStatus=").append(jobStatus).append("&");
    if (department != null)
      uriBuilder.append("department=").append(department).append("&");
    if (corporateDesignation != null)
      uriBuilder.append("corporateDesignation=").append(corporateDesignation).append("&");
    if (employmentType != null)
      uriBuilder.append("employmentType=").append(employmentType).append("&");
    if (unmapped != null)
      uriBuilder.append("unmapped=").append(unmapped).append("&");

    String uri = uriBuilder.toString();
    if (uri.endsWith("&")) {
      uri = uri.substring(0, uri.length() - 1);
    }

    return client.get()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .retrieve()
            .bodyToMono(Envelope.class);
  }

  /**
   * Get employees by list of IDs
   * Endpoint: GET /employee-information/employees-by-ids
   */
  public Mono<Envelope> getEmployeesByIds(java.util.List<String> employeeIds, int isDeleted) {
    String url = discoveryClient.getEmployeeServiceUrl();
    LOGGER.debug("Fetching employees by IDs: {}", employeeIds);

    StringBuilder uriBuilder = new StringBuilder(url + "employee-information/employees-by-ids?isDeleted=" + isDeleted);
    for (String id : employeeIds) {
      uriBuilder.append("&employeeIds=").append(id);
    }

    return client.get()
            .uri(uriBuilder.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .retrieve()
            .bodyToMono(Envelope.class);
  }

  /**
   * Get employee organizational details by employee ID
   * Endpoint: GET /organizational-details/employee/{employeeId}
   *
   * @param employeeId The employee ID
   * @return Mono<Envelope> containing organizational details
   */
  public Mono<Envelope> getOrganizationalDetailsByEmployeeId(String employeeId) {
    String url = discoveryClient.getEmployeeServiceUrl();
    LOGGER.debug("Fetching organizational details for employeeId: {}", employeeId);
    return client.get()
        .uri(url + "organizational-details/employee/" + employeeId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
        .retrieve()
        .bodyToMono(Envelope.class)
        .doOnError(error -> LOGGER.error("Error fetching organizational details for {}: {}",
            employeeId, error.getMessage()));
  }

  public Mono<Envelope> getEmployeesForSalaryStructure(String employeeId, String name) {
    String url = discoveryClient.getEmployeeServiceUrl();

    StringBuilder uriBuilder = new StringBuilder(url + "employee-information/salary-eligible-employees?");
    if (employeeId != null) uriBuilder.append("employeeId=").append(employeeId).append("&");
    if (name != null) uriBuilder.append("name=").append(name).append("&");

    String uri = uriBuilder.toString();
    if (uri.endsWith("&") || uri.endsWith("?")) {
      uri = uri.substring(0, uri.length() - 1);
    }

    return client.get()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .retrieve()
            .bodyToMono(Envelope.class)
            .doOnError(error -> LOGGER.error("Error fetching salary-eligible employees: {}", error.getMessage()));
  }


  /**
   * Get employee
   * Endpoint: GET /employee-information/get-employees-for-salary-porcess
   */
  public Mono<List<EmployeeResponse>> getEmpForSalaryProcess() {
    String url = discoveryClient.getEmployeeServiceUrl();
    return client.get()
        .uri(url + "employee-information/get-employees-for-salary-porcess")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
        .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<EmployeeResponse>>() {})
        .doOnError(error -> LOGGER.error("Error for employee fetching: {}", error.getMessage()));
  }
  public Mono<Envelope> searchFestivalEligibility(LocalDate fromDate, LocalDate toDate, String eligibility,
                                                  int page, int size, String sort) {
    String url = discoveryClient.getEmployeeServiceUrl();

    StringBuilder uriBuilder = new StringBuilder(url)
            .append("employee-information/festival-eligibility-search?")
            .append("page=").append(page)
            .append("&size=").append(size);

    if (sort != null && !sort.isBlank()) uriBuilder.append("&sort=").append(sort);
    if (fromDate != null) uriBuilder.append("&fromDate=").append(fromDate);
    if (toDate != null) uriBuilder.append("&toDate=").append(toDate);
    if (eligibility != null && !eligibility.isBlank()) uriBuilder.append("&eligibility=").append(eligibility);

    String uri = uriBuilder.toString();

    LOGGER.debug("Festival eligibility search URI: {}", uri);

    return client.get()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .retrieve()
            .bodyToMono(Envelope.class)
            .doOnError(error -> LOGGER.error("Error searching festival eligibility: {}", error.getMessage()));
  }

  public Mono<Envelope> getOrganizationalDetailsByEmployeeIds(List<String> employeeIds) {

    String url = discoveryClient.getEmployeeServiceUrl()
            + "organizational-details/by-employee-ids";

    EmployeeIdsRequest req = new EmployeeIdsRequest();
    req.setEmployeeIds(employeeIds);

    return client.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .bodyValue(req)
            .retrieve()
            .bodyToMono(Envelope.class);
  }

  public Mono<Envelope> searchFestivalEligible(LocalDate bonusDate, Integer minMonths,
                                               int page, int size, String sort) {
    String url = discoveryClient.getEmployeeServiceUrl();

    StringBuilder uriBuilder = new StringBuilder(url)
            .append("employee-information/festival-search?")
            .append("page=").append(page)
            .append("&size=").append(size);

    if (sort != null && !sort.isBlank()) uriBuilder.append("&sort=").append(sort);
    if (bonusDate != null) uriBuilder.append("&bonusDate=").append(bonusDate);
    if (minMonths != null) uriBuilder.append("&minMonths=").append(minMonths);

    String uri = uriBuilder.toString();

    LOGGER.debug("Festival eligible search URI: {}", uri);

    return client.get()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenClaimUtils.getToken())
            .retrieve()
            .bodyToMono(Envelope.class)
            .doOnError(error -> LOGGER.error("Error searching festival eligible: {}", error.getMessage()));
  }

}