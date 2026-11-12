package com.usfq.logistpulse.state;

public class ReservadoState implements BatchState {
    @Override
    public String getStateName() { return "RESERVADO"; }

    @Override
    public void ship(BatchContext context) {
        System.out.println("[State] Lote " + context.getBatchId() + ": RESERVADO -> ENVIADO");
        context.setState(new EnviadoState());
    }
}
