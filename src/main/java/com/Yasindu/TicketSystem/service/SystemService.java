package com.Yasindu.TicketSystem.service;

import com.Yasindu.TicketSystem.model.SystemConfiguration;
import com.Yasindu.TicketSystem.model.Ticketpool;
import com.Yasindu.TicketSystem.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing the system configuration and operations
 * This class handles saving, retrieving, clearing configurations and starting/ stopping
 * the ticketing system by managing thread operations
 */
@Service
public class SystemService {

    //Repository for storing and managing system configuration in the database
    private final SystemConfigRepository configRepository;
    private final ThreadService threadService; //Service for managing threads for vendors and customers

    /**
     *
     * @param configRepository
     * @param threadService
     */
    public SystemService(SystemConfigRepository configRepository, ThreadService threadService) {
        this.configRepository = configRepository;
        this.threadService = threadService;
    }

    /**
     * Saves a new system configuration to the database
     *
     * Clears any existing configuration to ensure only one active configuration is allowed
     * @param config The system configuration saved
     * @return The saved system configuration
     */
    public SystemConfiguration saveConfiguration(SystemConfiguration config) {
        //Clear the existing configuration to allow only one active configuration

        clearConfig();
        return configRepository.save(config);
    }

    //Retrieve the active system configuration, containing the active configuration if available
    public Optional<SystemConfiguration> getConfig() {
        return configRepository.findAll().stream().findFirst();
    }

    /**
     * Clears all the system configurations from the database, this ensures only
     * one configuration can be active at a time
     */

    public void clearConfig() {
        configRepository.deleteAll();
    }

    /**
     * Starts the ticketing system usimg the active configuration and ticket pool
     * @param config
     * @param ticketpool
     */
    public void startSystem(Optional<SystemConfiguration> config, Ticketpool ticketpool) {
        if (config.isPresent()) {
            SystemConfiguration configuration = config.get();
            ticketpool.setSellingComplete(false); //Ensure that the ticket selling is reset
            threadService.configure(configuration, ticketpool); //Configure the threads
            threadService.startThreads();
        }
        else{
            throw new IllegalStateException("No active configuration found. Please configure the system first.");
        }
    }

    //Stops the ticketing system by stopping all the active threads
    public void stopSystem() {
        threadService.stopThreads();
    }
}
