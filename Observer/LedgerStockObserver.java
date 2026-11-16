package com.usfq.logistpulse.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LedgerStockObserver {

    @EventListener
    public void onStockChanged(StockChangedEvent event) {
        System.out.printf("[Observer - Ledger] Auditoría registrada @ %s | SKU: %s | Lote: %s | Nuevo balance: %d%n",
            event.timestamp(), event.sku(), event.batchId(), event.remainingStock());
    }
}
