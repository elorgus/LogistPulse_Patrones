package com.usfq.logistpulse.observer;

import java.time.LocalDateTime;

public record StockChangedEvent(
    String sku,
    String batchId,
    int quantityReserved,
    int remainingStock,
    LocalDateTime timestamp
) {}
