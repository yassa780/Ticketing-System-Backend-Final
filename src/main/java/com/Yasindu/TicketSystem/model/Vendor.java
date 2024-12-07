package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final Ticketpool ticketpool;
    private final int ticketReleaseRate;
    private final LogService logService;

    public Vendor(Ticketpool ticketpool, SystemConfiguration config, LogService logService) {
        this.ticketpool = ticketpool;
        this.ticketReleaseRate = config.getTicketReleaseRate();
        this.logService = logService;
    }

    @Override
    public void run() {
        try {
            while (!ticketpool.isSellingComplete()) {
                ticketpool.addTickets(ticketReleaseRate);
                logService.addLog(Thread.currentThread().getName() + ": Added " + ticketReleaseRate + " tickets.");
                logger.info(Thread.currentThread().getName() + ": Added " + ticketReleaseRate + " tickets.");
                Thread.sleep(200); // Simulate ticket release delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logService.addLog(Thread.currentThread().getName() + ": Vendor interrupted.");
            logger.error(Thread.currentThread().getName() + ": Vendor interrupted.", e);
        }
    }
}
