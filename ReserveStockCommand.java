package com.usfq.logistpulse.command;

import com.usfq.logistpulse.receiver.InventoryReceiver;

public class ReserveStockCommand implements Command {
    private final String commandId;
    private final InventoryReceiver inventoryReceiver;
    private final String sku;
    private final int quantity;
    private final String strategy;
    private final String batchId;

    public ReserveStockCommand(String commandId, InventoryReceiver inventoryReceiver, 
                               String sku, int quantity, String strategy, String batchId) {
        this.commandId = commandId;
        this.inventoryReceiver = inventoryReceiver;
        this.sku = sku;
        this.quantity = quantity;
        this.strategy = strategy;
        this.batchId = batchId;
    }

    @Override
    public void execute() {
        // Delegates work straight to our Receiver
        inventoryReceiver.reserveStock(sku, quantity, strategy, batchId);
    }

    @Override
    public String getCommandId() { return this.commandId; }

    @Override
    public String getMetadata() {
        return String.format("Action=RESERVE, SKU=%s, Qty=%d, Strategy=%s, Batch=%s", 
            sku, quantity, strategy, batchId);
    }
}
