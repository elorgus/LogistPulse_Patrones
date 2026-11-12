package com.usfq.logistpulse.controller;

import com.usfq.logistpulse.chain.ReservationRequest;
import com.usfq.logistpulse.chain.ValidationChainService;
import com.usfq.logistpulse.chain.ValidationException;
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
    private final ValidationChainService validationChain;

    public ReservationController(InventoryReceiver inventoryReceiver, 
                                 DecisionLedger ledger, 
                                 ValidationChainService validationChain) {
        this.inventoryReceiver = inventoryReceiver;
        this.ledger = ledger;
        this.validationChain = validationChain;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody Map<String, Object> body) {
        String transactionId = UUID.randomUUID().toString();

        ReservationRequest request = new ReservationRequest(
            (String) body.get("sku"),
            (int) body.get("quantity"),
            (String) body.getOrDefault("batchId", "BATCH-01"),
            (boolean) body.getOrDefault("isBatchExpired", false),
            (boolean) body.getOrDefault("isBatchQuarantined", false)
        );
        String strategy = (String) body.getOrDefault("strategy", "FIFO");

        Command reserveCommand = new ReserveStockCommand(
            transactionId, inventoryReceiver, validationChain, request, strategy
        );

        try {
            ledger.logAndExecute(reserveCommand);
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "transactionId", transactionId,
                "message", "Reserva validada y registrada en el Ledger."
            ));
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "REJECTED",
                "transactionId", transactionId,
                "failedLink", ex.getFailedStep(),
                "reason", ex.getMessage()
            ));
        }
    }
}
