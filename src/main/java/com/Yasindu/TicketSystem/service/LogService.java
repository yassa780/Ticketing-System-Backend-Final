package com.Yasindu.TicketSystem.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing application logs
 *
 * This class provide methods to add logs, retrieve logs, and clear logs
 *
 */
@Service
public class LogService {

    // A thread safe list to store the logs
    private final List<String> logs = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds a new log entry to the log list
     * @param log The log message to be added
     */
    public void addLog(String log){
        logs.add(log);
    }

    /**
     * Retreives a copy of the current logs
     * A new list is returned to avoid concurrent modification issues when accessing logs
     * @return
     */
    public List <String> getLogs() {
        return new ArrayList<>(logs); //Returns a copy to avoid thread safety
    }

    /**
     * Clear all log entries from the log list
     */
    public void clearLogs() {
        logs.clear();
    }
}
