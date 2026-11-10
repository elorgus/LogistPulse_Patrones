package com.usfq.logistpulse.receiver;

import org.springframework.stereotype.Component;

@Component
public class InventoryReceiver {

    public void reserveStock(String sku, int quantity, String strategy, String batchId) {
        // Business logic execution matching your chain/strategy constraints (FEFO, FIFO, LIFO)
        System.out.printf("[Receiver] Successfully reserved %d units of SKU: %s using strategy: %s from Batch: %s%n", 
            quantity, sku, strategy, batchId);
    }

    public void releaseStock(String sku, int quantity) {
        System.out.printf("[Receiver] Released %d units of SKU: %s back to inventory.%n", quantity, sku);
    }
}
