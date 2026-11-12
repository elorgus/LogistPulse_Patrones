package com.usfq.logistpulse.chain;

import org.springframework.stereotype.Service;

@Service
public class ValidationChainService {
    private final ValidationHandler chainHead;

    public ValidationChainService() {
        // Build the exact sequence: SKU -> Stock -> Lote -> Capacidad
        ValidationHandler sku = new SkuHandler();
        ValidationHandler stock = new StockHandler();
        ValidationHandler lote = new LoteHandler();
        ValidationHandler capacidad = new CapacidadHandler();

        sku.setNext(stock).setNext(lote).setNext(capacidad);
        this.chainHead = sku;
    }

    public void executeChain(ReservationRequest request) {
        chainHead.validate(request);
    }
}
