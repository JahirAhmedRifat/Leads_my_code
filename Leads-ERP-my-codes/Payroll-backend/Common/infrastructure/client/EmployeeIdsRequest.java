package com.leads.microcube.infrastructure.client;

import lombok.*;

import java.util.List;

@Data
public class EmployeeIdsRequest {
    private List<String> employeeIds;
}
