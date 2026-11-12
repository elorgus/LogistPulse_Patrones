package com.usfq.logistpulse.strategy;

import java.util.List;

public interface PickingPolicy {
    InventoryBatch selectBatch(List<InventoryBatch> batches, int requestedQuantity);
}
