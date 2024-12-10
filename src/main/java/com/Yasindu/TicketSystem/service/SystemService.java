package com.Yasindu.TicketSystem.service;

import com.Yasindu.TicketSystem.model.SystemConfiguration;
import com.Yasindu.TicketSystem.model.Ticketpool;
import com.Yasindu.TicketSystem.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemService {

    private final SystemConfigRepository configRepository;
    private final ThreadService threadService;

    public SystemService(SystemConfigRepository configRepository, ThreadService threadService) {
        this.configRepository = configRepository;
        this.threadService = threadService;
    }

    //Saving the system configuration to the database
    public SystemConfiguration saveConfiguration(SystemConfiguration config) {
        //Clear the existing configuration to allow only one active configuration

        clearConfig();
        return configRepository.save(config);
    }

    //Retrieve the active system configuration, containing the active configuration if available
    public Optional<SystemConfiguration> getConfig() {
        return configRepository.findAll().stream().findFirst();
    }

    public void clearConfig() {
        configRepository.deleteAll();
    }

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

    public void stopSystem() {
        threadService.stopThreads();
    }
}
