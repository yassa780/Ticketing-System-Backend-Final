package com.Yasindu.TicketSystem.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration(); //Built in class

        //Allows the React frontend url
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        //Allow all HTTP Methods
        config.addAllowedMethod("*");

        //Allow all headers
        config.addAllowedHeader("*");

        //Allows credentials (if needed)
        config.setAllowCredentials(true);

        // Apply CORS configuration to specific paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter();
    }
}
