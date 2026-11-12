package com.usfq.logistpulse.state;

public class RecibidoState implements BatchState {
    @Override
    public String getStateName() { return "RECIBIDO"; }

    @Override
    public void store(BatchContext context) {
        System.out.println("[State] Lote " + context.getBatchId() + ": RECIBIDO -> ALMACENADO");
        context.setState(new AlmacenadoState());
    }
}
