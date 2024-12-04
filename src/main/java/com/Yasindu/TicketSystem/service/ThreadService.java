package com.Yasindu.TicketSystem.service;

import com.Yasindu.TicketSystem.model.Customer;
import com.Yasindu.TicketSystem.model.SystemConfiguration;
import com.Yasindu.TicketSystem.model.Ticketpool;
import com.Yasindu.TicketSystem.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private final LogService logService; //Injecting the LogService
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false;

    public ThreadService(LogService logService) {
        this.logService = logService;
    }

    public void configure(SystemConfiguration config, Ticketpool ticketpool){
        threads.clear();

        int ticketReleaseRate = config.getTicketReleaseRate();
        int customerRetrievalRate = config.getCustomerRetrievalRate();
        int maxCapacity = config.getMaxTicketCapacity();

        //Create vendor Threads
        for(int i = 0; i < config.getNumberOfVendors(); i++) {
            Thread vendorThread = new Thread(
                    new Vendor(ticketpool, ticketReleaseRate, maxCapacity, logService),
                    "Vendor-" + (i + 1)
            );
            threads.add(vendorThread);
        }

        //The customer threads
        for (int i = 0; i < config.getNumberOfCustomers(); i++){
            Thread customerThread = new Thread(
                    new Customer(ticketpool, customerRetrievalRate, logService),
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
