package com.leads.microcube.infrastructure.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

@Component
public class ServiceDiscoveryClient {

    private final LoadBalancerClient loadBalancerClient;

    public ServiceDiscoveryClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public String getPortalUrl() {
        return "http://localhost:8102/portal/v1/";
    }

    public String getEmployeeServiceUrl() {
        // ServiceInstance instance =
        // loadBalancerClient.choose("EMPLOYEE-MANAGEMENT-SERVICE");
       // return "http://192.168.10.56:8090/employee/v1/";
        return "http://localhost:8103/employee/v1/";
    }

    public String getObjectStorageServiceUrl() {
        ServiceInstance instance = loadBalancerClient.choose("LEADS-OBJECT-STORAGE-SERVER");
        return instance.getUri().toString() + "/minio/api/buckets";
    }

    public String getLocalEmployeeServiceUrl() {
        return "http://localhost:8103/employee/v1/";
    }

    public String getLeaveAttendanceServiceUrl() {
        // ServiceInstance instance =
        return "http://localhost:8105/leave-attendance/v1/";
    }
}
