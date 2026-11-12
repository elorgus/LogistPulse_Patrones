package com.usfq.logistpulse.chain;

public record ReservationRequest(
    String sku,
    int quantity,
    String batchId,
    boolean isBatchExpired,
    boolean isBatchQuarantined
) {}
