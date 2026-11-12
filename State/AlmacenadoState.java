package com.usfq.logistpulse.state;

public class AlmacenadoState implements BatchState {
    @Override
    public String getStateName() { return "ALMACENADO"; }

    @Override
    public void reserve(BatchContext context) {
        System.out.println("[State] Lote " + context.getBatchId() + ": ALMACENADO -> RESERVADO");
        context.setState(new ReservadoState());
    }

    @Override
    public void quarantine(BatchContext context) {
        System.out.println("[State] Lote " + context.getBatchId() + ": ALMACENADO -> CUARENTENA (Desvío)");
        context.setState(new CuarentenaState());
    }
}
