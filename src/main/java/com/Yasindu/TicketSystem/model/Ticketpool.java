package com.Yasindu.TicketSystem.model;

import com.Yasindu.TicketSystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Ticketpool {
    private static final Logger logger = LoggerFactory.getLogger(Ticketpool.class);
    private final Queue<String> tickets = new LinkedList<>();
    private final int maxCapacity;
    private final LogService logService;

    private boolean sellingComplete = false;
    private int totalTicketsRemaining;

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Ticketpool(LogService logService, SystemConfiguration config) {
        this.logService = logService;
        this.maxCapacity = config.getMaxTicketCapacity();
        this.totalTicketsRemaining = config.getTotalTickets();
    }

    public void addTickets(int numberOfTicketsToAdd) {
        lock.lock();
        try {
            while (tickets.size() >= maxCapacity) {
                logService.addLog("Ticketpool is full. Vendor is waiting.");
                logger.info("Ticketpool is full. Vendor is waiting.");
                notFull.await(); // Wait until space is available
                if (sellingComplete) {
                    return;
                }
            }

            int ticketsToAdd = Math.min(numberOfTicketsToAdd, totalTicketsRemaining);
            for (int i = 0; i < ticketsToAdd; i++) {
                tickets.add("Ticket-" + System.nanoTime());
            }

            totalTicketsRemaining -= ticketsToAdd;
            logService.addLog(ticketsToAdd + " tickets added. Total tickets: " + tickets.size());
            logger.info(ticketsToAdd + " tickets added. Total tickets: " + tickets.size());

            if (totalTicketsRemaining == 0) {
                logService.addLog("All tickets have been sold. Stopping ticket production.");
                logger.info("All tickets have been sold. Stopping ticket production.");
                sellingComplete = true;
            }
            notEmpty.signalAll(); // Notify customers
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Ticketpool addTickets interrupted", e);
        } finally {
            lock.unlock();
        }
    }

    public String removeTicket() {
        lock.lock();
        try {
            while (tickets.isEmpty() && !sellingComplete) {
                logService.addLog("No tickets available. Customer is waiting.");
                logger.info("No tickets available. Customer is waiting.");
                notEmpty.await(); // Wait for tickets to be added
            }

            if (tickets.isEmpty() && sellingComplete) {
                return null; // Graceful exit
            }

            String ticket = tickets.poll();
            logService.addLog("Ticket retrieved: " + ticket);
            logger.info("Ticket retrieved: " + ticket);
            notFull.signalAll(); // Notify vendors
            return ticket;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logService.addLog("Customer interrupted while retrieving tickets.");
            logger.error("Ticketpool removeTicket interrupted", e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean isSellingComplete() {
        lock.lock();
        try {
            return sellingComplete;
        } finally {
            lock.unlock();
        }
    }

    public int getAvailableTickets() {
        lock.lock();
        try {
            return tickets.size();
        } finally {
            lock.unlock();
        }
    }

    public void setSellingComplete(boolean complete) {
        lock.lock();
        try{
            this.sellingComplete = complete;
            notEmpty.signalAll(); //Notify waiting customers
            notFull.signalAll();
        }
        finally{
            lock.unlock();
        }
    }
}
