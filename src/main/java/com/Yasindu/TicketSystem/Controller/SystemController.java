package com.Yasindu.TicketSystem.Controller;

import com.Yasindu.TicketSystem.model.SystemConfiguration;
import com.Yasindu.TicketSystem.model.Ticketpool;
import com.Yasindu.TicketSystem.service.LogService;
import com.Yasindu.TicketSystem.service.SystemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller to handle system configuration, start/stop operations, and log management
 */
@RestController
@RequestMapping("/api")
public class SystemController {
    private final SystemService systemService;
    private final Ticketpool ticketpool;
    private final LogService logService; // Inject LogService

    /**
     * The constructor to inject required services and components
     * @param systemService
     * @param ticketpool
     * @param logService
     */
    public SystemController(SystemService systemService, Ticketpool ticketpool, LogService logService) {
        this.systemService = systemService;
        this.ticketpool = ticketpool;
        this.logService = logService;
    }

    /**
     * Configures the system with provided parameters and clears existing logs
     * @param config . Config the system configuration details sent in the request body
     * @return a response containing the total tickets available
     */

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> configureSystem(@RequestBody @Valid SystemConfiguration config){
        systemService.clearConfig();
        SystemConfiguration savedConfig = systemService.saveConfiguration(config);

        //Clear logs during reconfiguration
        logService.clearLogs();

        // Constructs the response to include ticketsAvailable (totalTickets)
        Map<String, Object> response = new HashMap<>();
        response.put("ticketsAvailable", savedConfig.getTotalTickets());

        // Log the configuration action
        logService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current system configuration.
     * @return the active configuration or a 404 if none exists
     */

    @GetMapping("/config")
    public ResponseEntity<SystemConfiguration> getConfig() {
        return systemService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Starts the system if a valid configuration exists.
     *
     * @return a response indicating success or a 400 error if no configuration is found
     */

    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        Optional<SystemConfiguration> config = systemService.getConfig();
        if (config.isEmpty()) {
            return ResponseEntity.badRequest().body("Configuration not found. Please configure the system first.");
        }
        systemService.startSystem(config, ticketpool);

        // This will Log the start action
        logService.addLog("System started successfully.");

        return ResponseEntity.ok("System started successfully!");
    }

    /**
     * Stops the system and logs the action
     *
     * @return a response indicating success
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        systemService.stopSystem();

        // This will Log the stop action
        logService.addLog("System stopped successfully.");

        return ResponseEntity.ok("System stopped successfully!");
    }

    /**
     * Fetches the list of logs.
     *
     * @return a list of log entires
     */
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = logService.getLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * Clears all the logs from the system
     *
     * @return a response indicating success
     */
    @DeleteMapping("/logs")
    public ResponseEntity<String> clearLogs() {
        logService.clearLogs();
        return ResponseEntity.ok("Logs cleared successfully!");
    }
}
