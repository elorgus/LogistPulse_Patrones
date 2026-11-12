package com.usfq.logistpulse.state;

public class BatchContext {
    private final String batchId;
    private final String sku;
    private BatchState currentState;

    public BatchContext(String batchId, String sku) {
        this.batchId = batchId;
        this.sku = sku;
        this.currentState = new RecibidoState(); // Every batch starts in Recibido
    }

    public void setState(BatchState newState) {
        this.currentState = newState;
    }

    public String getBatchId() { return batchId; }
    public String getSku() { return sku; }
    public String getCurrentStateName() { return currentState.getStateName(); }

    public void store() { currentState.store(this); }
    public void quarantine() { currentState.quarantine(this); }
    public void reserve() { currentState.reserve(this); }
    public void ship() { currentState.ship(this); }
}
