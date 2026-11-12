package com.usfq.logistpulse.chain;

public class ValidationException extends RuntimeException {
    private final String failedStep;

    public ValidationException(String failedStep, String reason) {
        super(reason);
        this.failedStep = failedStep;
    }

    public String getFailedStep() { return failedStep; }
}
