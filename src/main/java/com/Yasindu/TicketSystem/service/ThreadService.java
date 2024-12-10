package com.Yasindu.TicketSystem.service;

import com.Yasindu.TicketSystem.model.Customer;
import com.Yasindu.TicketSystem.model.SystemConfiguration;
import com.Yasindu.TicketSystem.model.Ticketpool;
import com.Yasindu.TicketSystem.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing the lifecycle of threads for vendors and customers
 * This service creates, starts and stops threads based on the system configuration
 */
@Service
public class ThreadService {

    // Service for logging thread-related activities
    private final LogService logService;

    private List<Thread> threads = new ArrayList<>(); //List of threads for vendors and customers
    private volatile boolean running = false; //Flag to track whether threads are currently running

    public ThreadService(LogService logService) {
        this.logService = logService;
    }

    /**
     * Configures the system by creating threads for vendors and customers
     *
     * @param config
     * @param ticketpool
     */
    public void configure(SystemConfiguration config, Ticketpool ticketpool){
        threads.clear();


        //Create vendor Threads
        for(int i = 0; i < config.getNumberOfVendors(); i++) {
            Thread vendorThread = new Thread(
                    new Vendor(ticketpool, config, logService),
                    "Vendor-" + (i + 1)
            );
            threads.add(vendorThread);
        }

        //The customer threads
        for (int i = 0; i < config.getNumberOfCustomers(); i++){
            Thread customerThread = new Thread(
                    new Customer(ticketpool, config, logService),
                    "Customer-" + (i + 1)
            );
            threads.add(customerThread);
        }
        logService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
    }

    public void startThreads() {
        if (running) {
            throw new IllegalStateException("Threads are already running.");
        }
        running = true;

        for(Thread thread: threads){
            thread.start();
        }

        logService.addLog("All threads started");
    }

    public void stopThreads() {
        if (!running) {
            throw new IllegalStateException("Threads are not running.");
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }
        running = false;

        logService.addLog("All threads stopped.");
    }
}
