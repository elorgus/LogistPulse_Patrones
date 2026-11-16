package com.usfq.logistpulse.command;

import com.usfq.logistpulse.observer.StockChangedEvent;
import com.usfq.logistpulse.state.BatchContext;
import com.usfq.logistpulse.strategy.InventoryBatch;
import com.usfq.logistpulse.strategy.PickingContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryReceiver {

    private final PickingContext pickingContext;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<String, BatchContext> batchStates = new ConcurrentHashMap<>();
    private int totalSimulatedStock = 100;

    public InventoryReceiver(PickingContext pickingContext, ApplicationEventPublisher eventPublisher) {
        this.pickingContext = pickingContext;
        this.eventPublisher = eventPublisher;

        initStoredBatch("LOTE-OLD-ARRIVAL", "SKU-9001");
        initStoredBatch("LOTE-EXPIRES-SOON", "SKU-9001");
        initStoredBatch("LOTE-NEWEST", "SKU-9001");
    }

    private void initStoredBatch(String batchId, String sku) {
        BatchContext ctx = new BatchContext(batchId, sku);
        ctx.store();
        batchStates.put(batchId, ctx);
    }

    public void reserveStock(String sku, int quantity, String strategy, String requestedBatchId) {
        List<InventoryBatch> availableBatches = List.of(
            new InventoryBatch("LOTE-OLD-ARRIVAL", sku, 40, LocalDate.of(2026, 1, 10), LocalDate.of(2027, 6, 1)),
            new InventoryBatch("LOTE-EXPIRES-SOON", sku, 30, LocalDate.of(2026, 3, 15), LocalDate.of(2026, 12, 1)),
            new InventoryBatch("LOTE-NEWEST", sku, 50, LocalDate.of(2026, 10, 20), LocalDate.of(2028, 1, 15))
        );

        // 1. Strategy selects the batch
        InventoryBatch chosenBatch = pickingContext.executePicking(strategy, availableBatches, quantity);

        // 2. State transitions batch lifecycle (ALMACENADO -> RESERVADO)
        BatchContext stateContext = batchStates.computeIfAbsent(
            chosenBatch.batchId(), id -> new BatchContext(id, sku)
        );
        stateContext.reserve();

        // 3. Deduct stock and publish StockChangedEvent to all Observers
        totalSimulatedStock = Math.max(0, totalSimulatedStock - quantity);
        eventPublisher.publishEvent(new StockChangedEvent(
            sku,
            chosenBatch.batchId(),
            quantity,
            totalSimulatedStock,
            LocalDateTime.now()
        ));
    }

    public BatchContext getBatchContext(String batchId) {
        return batchStates.get(batchId);
    }
}
