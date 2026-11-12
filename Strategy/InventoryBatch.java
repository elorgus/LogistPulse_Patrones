package com.usfq.logistpulse.strategy;

import java.time.LocalDate;

public record InventoryBatch(
    String batchId,
    String sku,
    int availableUnits,
    LocalDate arrivalDate,
    LocalDate expirationDate
) {}
