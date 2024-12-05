package com.Yasindu.TicketSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
/**
 * This is the Configuration class to handle Cross-Origin Resource Sharing (CORS) settings.
 * This ensures the backend API can interact with the frontend hosted on a different domain
 *
 */
public class CorsConfig {

    /**
     * The Bean annotation is to configure and apply CORS settings globally
     * @return CorsFilter - The configured CORS filter to be applied to incoming requests
     */

    @Bean
    public CorsFilter corsFilter() {

        //Creates a new CORS configuration instance
        CorsConfiguration config = new CorsConfiguration();

        /**
         * Allow specific origins
         * In this case, the React frontend running on the URL below
         * The URL can be change according to the port it is being runned on
         */
        config.setAllowedOrigins(Arrays.asList("http://localhost:5174"));

        //Allow all HTTP Methods (GET, POST, PUT, DELETE, etc.)
        config.addAllowedMethod("*");

        //Allow all HTTP headers
        config.addAllowedHeader("*");

        /**
         * Allow credentials (e.g., cookies, authorization headers).
         * Its set to true if your applications uses credentials during API requests.
         */
        config.setAllowCredentials(true);

        /**
         * Associate this CORS configuration with API routes.
         */

        // Apply CORS configuration to specific paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        //Return the CORS filter with the applied configuration
        return new CorsFilter(source);
    }
}
