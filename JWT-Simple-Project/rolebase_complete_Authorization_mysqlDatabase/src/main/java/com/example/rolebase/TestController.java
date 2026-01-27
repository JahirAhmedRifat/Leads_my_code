package com.example.rolebase;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "Welcome to User Dashboard";
    }
}

