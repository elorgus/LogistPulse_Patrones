package com.usfq.logistpulse.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PulseFleetObserver {

    @EventListener
    public void onStockChanged(StockChangedEvent event) {
        System.out.printf("[Observer - PULSEFLEET] Asignando robot AMR para recoger %d unidades del lote %s (Actualizando gemelo 3D).%n",
            event.quantityReserved(), event.batchId());
    }
}
