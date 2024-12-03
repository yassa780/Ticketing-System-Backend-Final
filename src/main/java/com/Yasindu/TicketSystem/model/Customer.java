package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final Ticketpool ticketpool;
    private final int retrievalLimit;
    private final LogService logService;

    public Customer(Ticketpool ticketpool, int retrievalLimit, LogService logService){
        this.ticketpool = ticketpool;
        this.retrievalLimit = retrievalLimit;
        this.logService = logService;
    }

    @Override
    public void run(){
        for (int i = 0; i < retrievalLimit; i++) {
            try {
                synchronized (ticketpool) {
                    while (ticketpool.getAvailableTickets() == 0) {
                        String logMessage = Thread.currentThread().getName() + ": No tickets available. Waiting.";
                        logger.info(logMessage);
                        logService.addLog(logMessage);
                        ticketpool.wait(); // Wait for tickets to be added
                    }

                    String ticket = ticketpool.removeTicket();
                    String logMessage = Thread.currentThread().getName() + ": Retrieved " + ticket;
                    logger.info(logMessage);
                    logService.addLog(logMessage);

                    ticketpool.notifyAll(); // Notify vendors to add more tickets
                }

                Thread.sleep(200); // Simulate delay in ticket retrieval
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String logMessage = Thread.currentThread().getName() + ": Customer interrupted.";
                logger.error(logMessage, e);
                logService.addLog(logMessage);
                break;
            }
        }

        String logMessage = Thread.currentThread().getName() + ": Finished purchasing tickets.";
        logger.info(logMessage);
        logService.addLog(logMessage);
    }

}
