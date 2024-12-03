package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final Ticketpool ticketpool;
    private final int ticketsToRelease;
    private final int maxCapacity;
    private final LogService logService;

    public Vendor(Ticketpool ticketpool, int ticketsToRelease, int maxCapacity, LogService logService){
        this.ticketpool = ticketpool;
        this.ticketsToRelease = ticketsToRelease;
        this.maxCapacity = maxCapacity;
        this.logService = logService;
    }

    @Override
    public void run() {
        for (int i = 0; i < ticketsToRelease; i++) {
            synchronized (ticketpool) {
                while (ticketpool.getAvailableTickets() >= maxCapacity) {
                    try {
                        String logMessage = Thread.currentThread().getName() + ": Max ticket capacity reached. Waiting.";
                        logger.info(logMessage);
                        logService.addLog(logMessage);

                        ticketpool.wait(); // Wait for the pool to have space
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        String logMessage = Thread.currentThread().getName() + ": Vendor interrupted while waiting.";
                        logger.error(logMessage, e);
                        logService.addLog(logMessage);
                        return; // Exit the thread if interrupted
                    }
                }

                // Add a ticket to the pool
                String ticket = "Ticket-" + System.nanoTime();
                ticketpool.addTicket(ticket);

                String logMessage = Thread.currentThread().getName() + ": Added " + ticket;
                logger.info(logMessage);
                logService.addLog(logMessage);

                // Notify customers waiting for tickets
                ticketpool.notifyAll();
            }

            // Simulate delay in ticket release
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String logMessage = Thread.currentThread().getName() + ": Vendor interrupted during sleep.";
                logger.error(logMessage, e);
                logService.addLog(logMessage);
                return; // Exit the thread if interrupted
            }
        }
    }
}
