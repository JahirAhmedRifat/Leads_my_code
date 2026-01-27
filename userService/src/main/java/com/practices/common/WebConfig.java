package com.practices.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // সব API endpoint-এর জন্য প্রযোজ্য
//                .allowedOriginPatterns("https://your-frontend.com", "https://mobile-app.com") // নির্দিষ্ট origin
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // নিরাপদ HTTP methods
//                .allowedHeaders("Authorization", "Content-Type", "Accept") // শুধুমাত্র প্রয়োজনীয় হেডার
//                .allowCredentials(true); // Cookie / Token allow করা
//    }


}
