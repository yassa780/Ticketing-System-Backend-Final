package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final Ticketpool ticketpool;
    private final int customerRetrievalRate;
    private final LogService logService;

    public Customer(Ticketpool ticketpool, SystemConfiguration config, LogService logService) {
        this.ticketpool = ticketpool;
        this.customerRetrievalRate = config.getCustomerRetrievalRate();
        this.logService = logService;
    }

    @Override
    public void run() {
        try {
            while (!ticketpool.isSellingComplete() || ticketpool.getAvailableTickets() > 0) {
                for (int i = 0; i < customerRetrievalRate; i++) {
                    String ticket = ticketpool.removeTicket();
                    if (ticket == null) {
                        break;
                    }
                    logService.addLog(Thread.currentThread().getName() + ": Retrieved ticket " + ticket);
                    logger.info(Thread.currentThread().getName() + ": Retrieved ticket " + ticket);
                }
                Thread.sleep(300); // Simulate retrieval delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logService.addLog(Thread.currentThread().getName() + ": Customer interrupted.");
            logger.error(Thread.currentThread().getName() + ": Customer interrupted.", e);
        }
    }
}
