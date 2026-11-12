package com.usfq.logistpulse.chain;

public abstract class ValidationHandler {
    private ValidationHandler next;

    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public abstract void validate(ReservationRequest request);

    protected void checkNext(ReservationRequest request) {
        if (next != null) {
            next.validate(request);
        }
    }
}
