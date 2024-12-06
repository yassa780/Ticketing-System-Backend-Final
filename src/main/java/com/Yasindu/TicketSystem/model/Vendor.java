package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final Ticketpool ticketpool;
    private final int ticketReleaseRate;
    private final int maxCapacity;
    private final LogService logService;

    public Vendor(Ticketpool ticketpool, SystemConfiguration config, LogService logService){
        this.ticketpool = ticketpool;
        this.ticketReleaseRate = config.getTicketReleaseRate();
        this.maxCapacity = config.getMaxTicketCapacity();
        this.logService = logService;
    }

    @Override
    public void run() {
        try{
            while(!ticketpool.isSellingComplete()) {
                synchronized (ticketpool){
                    //Stop if the ticketpool has reached the maximum Capacity
                    if (ticketpool.getAvailableTickets() >= maxCapacity){
                        logService.addLog(Thread.currentThread().getName() + " Ticketpool full. Waiting");
                        logger.info(Thread.currentThread().getName() + ": Ticketpool full. Waiting");
                        ticketpool.wait(); //Wait until there's room in the pool
                    }
                    //Calculate jow many tickets to add
                    int ticketsToAdd = Math.min(ticketReleaseRate, maxCapacity - ticketpool.getAvailableTickets());

                    //Stop adding tickets if no more can be added
                    if (ticketsToAdd <=0) {
                        break;
                    }

                    ticketpool.addTicket(ticketsToAdd);

                    logService.addLog(Thread.currentThread().getName() + ": Added " + ticketReleaseRate + " tickets.");
                    logger.info(Thread.currentThread().getName() + ": Added " + ticketReleaseRate + " tickets.");
                }

                Thread.sleep(200); //Stimulate ticket release delay
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logService.addLog(Thread.currentThread().getName() + ": Vendor interrupted.");
            logger.error(Thread.currentThread().getName() + ": Vendor interrupted.", e);
        }

    }
}
