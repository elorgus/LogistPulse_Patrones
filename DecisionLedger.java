package com.usfq.logistpulse.ledger;

import com.usfq.logistpulse.command.Command;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DecisionLedger {
    private final List<String> auditLogs = new ArrayList<>();

    public void logAndExecute(Command command) {
        // Log the decision audit trail as required by your slides
        String logEntry = String.format("[LEDGER LOG] ID: %s | Details: %s", 
            command.getCommandId(), command.getMetadata());
        auditLogs.add(logEntry);
        System.out.println(logEntry);
        
        // Execute the command
        command.execute();
    }
}
