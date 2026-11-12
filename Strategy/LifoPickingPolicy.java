package com.usfq.logistpulse.strategy;

import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component("LIFO")
public class LifoPickingPolicy implements PickingPolicy {
    @Override
    public InventoryBatch selectBatch(List<InventoryBatch> batches, int requestedQuantity) {
        return batches.stream()
            .filter(b -> b.availableUnits() >= requestedQuantity)
            .max(Comparator.comparing(InventoryBatch::arrivalDate))
            .orElseThrow(() -> new IllegalStateException("No hay lote disponible con stock suficiente (LIFO)."));
    }
}
