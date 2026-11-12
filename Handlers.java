package com.usfq.logistpulse.chain;

// Link 1: SKU Validation
public class SkuHandler extends ValidationHandler {
    @Override
    public void validate(ReservationRequest request) {
        if (request.sku() == null || request.sku().isBlank()) {
            throw new ValidationException("SKU", "El código SKU no puede estar vacío o no existe.");
        }
        checkNext(request);
    }
}

// Link 2: Stock Validation
class StockHandler extends ValidationHandler {
    @Override
    public void validate(ReservationRequest request) {
        int availableStock = 100; // Simulated DB lookup
        if (request.quantity() <= 0 || request.quantity() > availableStock) {
            throw new ValidationException("Stock", "Stock insuficiente para cubrir la reserva solicitada.");
        }
        checkNext(request);
    }
}

// Link 3: Lote (Batch) Validation (Vencido o en cuarentena)
class LoteHandler extends ValidationHandler {
    @Override
    public void validate(ReservationRequest request) {
        if (request.isBatchExpired()) {
            throw new ValidationException("Lote", "El lote " + request.batchId() + " está vencido.");
        }
        if (request.isBatchQuarantined()) {
            throw new ValidationException("Lote", "El lote " + request.batchId() + " se encuentra en cuarentena.");
        }
        checkNext(request);
    }
}

// Link 4: Capacidad Validation
class CapacidadHandler extends ValidationHandler {
    @Override
    public void validate(ReservationRequest request) {
        int maxZoneCapacity = 50; // Simulated warehouse/AMR limit
        if (request.quantity() > maxZoneCapacity) {
            throw new ValidationException("Capacidad", "La reserva excede la capacidad operativa de despacho.");
        }
        checkNext(request);
    }
}
