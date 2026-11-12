package com.usfq.logistpulse.command;

import com.usfq.logistpulse.chain.ReservationRequest;
import com.usfq.logistpulse.chain.ValidationChainService;
import com.usfq.logistpulse.receiver.InventoryReceiver;

public class ReserveStockCommand implements Command {
    private final String commandId;
    private final InventoryReceiver inventoryReceiver;
    private final ValidationChainService validationChain;
    private final ReservationRequest request;
    private final String strategy;

    public ReserveStockCommand(String commandId, InventoryReceiver inventoryReceiver,
                               ValidationChainService validationChain,
                               ReservationRequest request, String strategy) {
        this.commandId = commandId;
        this.inventoryReceiver = inventoryReceiver;
        this.validationChain = validationChain;
        this.request = request;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        // 1. Run the Chain of Responsibility guardrails first
        validationChain.executeChain(request);
        
        // 2. Only executes if all 4 chain links pass!
        inventoryReceiver.reserveStock(request.sku(), request.quantity(), strategy, request.batchId());
    }

    @Override
    public String getCommandId() { return this.commandId; }

    @Override
    public String getMetadata() {
        return String.format("Action=RESERVE, SKU=%s, Qty=%d, Batch=%s", 
            request.sku(), request.quantity(), request.batchId());
    }
}
