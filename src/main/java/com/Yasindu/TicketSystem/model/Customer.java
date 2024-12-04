package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final Ticketpool ticketpool;
    private final int customerRetrievalRate;
    private final LogService logService;

    public Customer(Ticketpool ticketpool, int customerRetrievalRate, LogService logService){
        this.ticketpool = ticketpool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.logService = logService;
    }

    @Override
    public void run() {
        try{
            while(!ticketpool.isSellingComplete() || ticketpool.getAvailableTickets() > 0){
                synchronized (ticketpool){
                    String ticket = ticketpool.removeTicket();
                    if (ticket == null) {
                        break;
                    }

                    logService.addLog(Thread.currentThread().getName() + ": Retrieval ticket " + ticket);
                    logger.info(Thread.currentThread().getName() + ": Retrievd ticket" + ticket);
                }

                Thread.sleep(300); //Stimulate retrieval delay
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logService.addLog(Thread.currentThread().getName() + ": Customer interrupted.");
            logger.error(Thread.currentThread().getName() + ": Customer interrupted.", e);
        }

    }
}
