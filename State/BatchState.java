package com.usfq.logistpulse.state;

public interface BatchState {
    String getStateName();

    default void store(BatchContext context) {
        throw new IllegalStateException("Transición inválida: no se puede almacenar desde " + getStateName());
    }

    default void quarantine(BatchContext context) {
        throw new IllegalStateException("Transición inválida: no se puede enviar a cuarentena desde " + getStateName());
    }

    default void reserve(BatchContext context) {
        throw new IllegalStateException("Transición inválida: no se puede reservar desde " + getStateName());
    }

    default void ship(BatchContext context) {
        throw new IllegalStateException("Transición inválida: no se puede enviar desde " + getStateName());
    }
}
