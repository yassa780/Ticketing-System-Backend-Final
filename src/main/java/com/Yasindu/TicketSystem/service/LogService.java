package com.Yasindu.TicketSystem.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LogService {

    private final List<String> logs = Collections.synchronizedList(new ArrayList<>());

    public void addLog(String log){
        logs.add(log);
    }

    public List <String> getLogs() {
        return new ArrayList<>(logs); //Returuns a copy to avoid concurrent modification
    }

    public void clearLogs() {
        logs.clear();
    }
}
