package com.usfq.logistpulse.controller;

import com.usfq.logistpulse.command.Command;
import com.usfq.logistpulse.command.ReserveStockCommand;
import com.usfq.logistpulse.ledger.DecisionLedger;
import com.usfq.logistpulse.receiver.InventoryReceiver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final InventoryReceiver inventoryReceiver;
    private final DecisionLedger ledger;

    // Spring Boot automatically injects the Receiver and Ledger singletons
    public ReservationController(InventoryReceiver inventoryReceiver, DecisionLedger ledger) {
        this.inventoryReceiver = inventoryReceiver;
        this.ledger = ledger;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody Map<String, Object> requestBody) {
        String transactionId = UUID.randomUUID().toString();
        
        // Extract incoming data parameters
        String sku = (String) requestBody.get("sku");
        int quantity = (int) requestBody.get("quantity");
        String strategy = (String) requestBody.getOrDefault("strategy", "FIFO"); // FEFO, FIFO, LIFO
        String batchId = (String) requestBody.getOrDefault("batchId", "BATCH-DEFAULT");

        // 1. Create the concrete command (Configured with parameters and its receiver)
        Command reserveCommand = new ReserveStockCommand(
            transactionId, inventoryReceiver, sku, quantity, strategy, batchId
        );

        // 2. Pass it to the ledger invoker pipeline to execute and save log tracking state
        ledger.logAndExecute(reserveCommand);

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "transactionId", transactionId,
            "message", "Reservation registered in Ledger successfully."
        ));
    }
}
