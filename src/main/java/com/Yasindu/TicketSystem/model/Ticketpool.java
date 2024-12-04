package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class Ticketpool {

    private static final Logger logger = LoggerFactory.getLogger(Ticketpool.class);
    private final Queue<String> tickets = new LinkedList<>();
    private final int maxCapacity;
    private boolean sellingComplete = false;

    @Autowired
    private final LogService logService; //Inject the LogService

    public Ticketpool(LogService logService){
        this.logService = logService;
        this.maxCapacity = Integer.MAX_VALUE;
    }

    public synchronized void addTicket(int numberOfTickets) {
       try{
           //Will wait if the ticketpool is full
           while(tickets.size() >= maxCapacity) {
               logService.addLog("Ticketpool is full. Vendor is waiting.");
               logger.info("Ticketpool is full. Vendor is waiting.");
               wait();
               if (sellingComplete){
                   return; //Exit if selling is complete
               }
           }

           //Calculate the actual number of tickes that can be added
           int ticketsToAdd = Math.min(numberOfTickets, maxCapacity - tickets.size());

           for(int i = 0; i < numberOfTickets; i++){
               tickets.add("Ticket - " + System.nanoTime());
           }
           logService.addLog(numberOfTickets + " tickets added. Total: " + tickets.size());
           logger.info(numberOfTickets + " tickets added. Total: " + tickets.size());
           notifyAll();
       }
       catch (InterruptedException e){
           Thread.currentThread().interrupt();
           logger.error("Ticketpool add interrupted", e);
       }
    }




    public synchronized String removeTicket() throws InterruptedException{
        while (tickets.isEmpty() && !sellingComplete){
            logService.addLog("No tickets available. Customer is waiting.");
            logger.info("No tickets available. Customer is waiting");
            wait();
        }

        if (tickets.isEmpty() && sellingComplete) {
            return null;
        }

        String ticket = tickets.poll();
        logService.addLog("Tickets retrieved: " + ticket);
        logger.info("Ticket retrieved: " + ticket);
        notifyAll();
        return ticket;


    }
    public synchronized int getAvailableTickets() {
        return tickets.size();
    }

    public synchronized void setSellingComplete(boolean complete) {
        this.sellingComplete = complete;
        notifyAll();
    }

    public synchronized boolean isSellingComplete(){
        return sellingComplete;
    }
}
