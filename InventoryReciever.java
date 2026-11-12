package com.usfq.logistpulse.receiver;

import com.usfq.logistpulse.strategy.InventoryBatch;
import com.usfq.logistpulse.strategy.PickingContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InventoryReceiver {

    private final PickingContext pickingContext;

    public InventoryReceiver(PickingContext pickingContext) {
        this.pickingContext = pickingContext;
    }

    public void reserveStock(String sku, int quantity, String strategy, String requestedBatchId) {
        // Simulated warehouse batches for the requested SKU
        List<InventoryBatch> availableBatches = List.of(
            new InventoryBatch("LOTE-OLD-ARRIVAL", sku, 40, LocalDate.of(2026, 1, 10), LocalDate.of(2027, 6, 1)),
            new InventoryBatch("LOTE-EXPIRES-SOON", sku, 30, LocalDate.of(2026, 3, 15), LocalDate.of(2026, 12, 1)),
            new InventoryBatch("LOTE-NEWEST", sku, 50, LocalDate.of(2026, 10, 20), LocalDate.of(2028, 1, 15))
        );

        // Delegate batch selection to the active Strategy
        InventoryBatch chosenBatch = pickingContext.executePicking(strategy, availableBatches, quantity);

        System.out.printf("[Receiver] Reserved %d units of %s using policy [%s] -> Selected Batch: %s (Expires: %s)%n",
            quantity, sku, strategy.toUpperCase(), chosenBatch.batchId(), chosenBatch.expirationDate());
    }

    public void releaseStock(String sku, int quantity) {
        System.out.printf("[Receiver] Released %d units of SKU: %s back to inventory.%n", quantity, sku);
    }
}
